# Yoga Matching API — Team 14

A production-ready yoga pose matching system with AI-powered studio chatbot, live at
**[match.yogaman.club](https://match.yogaman.club)** · **[elbee.yogaman.club](https://elbee.yogaman.club)**

---

## Project Overview

This project is a full-stack yoga recommendation platform with two core layers:

| Layer | Technology | Purpose |
|---|---|---|
| **Matching API** | Spring Boot 3.x, Java 21, PostgreSQL | Pose scoring, Kill-Switch safety, geo-search |
| **Studio Chatbot** | FastAPI, Python 3.11, Ollama (Mistral 7B) | GEO-book RAG chatbot (`elbee`) |

### Pose Matching Score Formula

The matching engine combines four weighted signals:

```
score = 0.40 × benefit + 0.25 × goal + 0.20 × level + 0.15 × time
```

The **Kill-Switch** blocks any pose with a CRITICAL contraindication matching an
active health flag — regardless of score.

---

## Repository Structure

```
yogaqueue/
+-- java/
¦   +-- matching/
¦   ¦   +-- MatchService.java          # Core scoring engine + Kill-Switch
¦   ¦   +-- MatchController.java       # REST endpoints: POST /api/v1/match
¦   ¦   +-- MatchRequest.java          # Request DTO
¦   ¦   +-- MatchResult.java           # Response DTO (scored / blocked)
¦   +-- instructor/
¦   ¦   +-- InstructorController.java  # CRUD + Schema.org JSON-LD (E-E-A-T)
¦   +-- studio/
¦       +-- StudioService.java         # Haversine geo-distance search
+-- ai/
    +-- rag_service.py                 # Keyword + phrase RAG over GEO-book pages
    +-- llm_service.py                 # Ollama async client + OpenAI fallback
```

---

## File Descriptions

### `java/matching/MatchService.java`
Core matching engine. Maps user **goals** to pose benefit tags via `GOAL_TAG_MAP`
(8 goals: `Stress_Relief`, `Spinal_Mobility`, `Hip_Flexibility`, `Core_Strength`,
`Balance`, `Breathing`, `Sleep`, `Energy`). Filters the full pose catalogue by
`maxDifficulty` (BEGINNER=2, INTERMEDIATE=3, ADVANCED=5) and `availableMinutes`
(difficulty × 3 minute heuristic). For each candidate it runs the Kill-Switch first,
then computes a weighted benefit-intersection score. Returns the top-K results sorted
descending by score.

### `java/matching/MatchController.java`
Spring `@RestController` mapped to `/api/v1/match`.
- `GET  /api/v1/match/status` — health / placeholder check.
- `POST /api/v1/match` — accepts `MatchRequest`, returns `MatchResponse`.

### `java/matching/MatchRequest.java`
Inbound DTO. Fields:
- `healthFlags` — list of active health condition flags (used for Kill-Switch).
- `goals` — list of goal strings from the UI selector.
- `experienceLevel` — `BEGINNER | INTERMEDIATE | ADVANCED` (default `INTERMEDIATE`).
- `availableMinutes` — session time budget in minutes (default 60).
- `topK` — max results, 1–100 (default 10).

### `java/matching/MatchResult.java`
Outbound DTO per pose. Carries `poseId`, `canonicalName`, `commonName`,
`difficultyRank`, `naturalDescription`, `score`, `blocked` flag, and `reason`
(populated when Kill-Switch fires). Built via static factories `scored()` and
`blocked()`.

### `java/instructor/InstructorController.java`
CRUD for the `instructors` table with optional filter by `city` or `specialty`.
The `GET /{id}/jsonld` endpoint emits a Schema.org `Person` JSON-LD document for
E-E-A-T signals in AI search engines (Google SGE, Perplexity, Bing Copilot).

### `java/studio/StudioService.java`
Haversine great-circle distance calculation (WGS-84, Earth radius 6371 km).
`findNearby(lat, lng, radiusKm)` streams all studios, excludes null coordinates,
maps to `StudioDistance` records, and returns sorted by ascending distance.

### `ai/rag_service.py`
Keyword-overlap retrieval over the GEO-book `GeoDataStore`. Scores pages by unique
query token hits, adds +3 phrase-match bonus, then assembles a context string
(char-limited to avoid LLM context overflow). Builds the Ollama `/api/chat` messages
list with an **Elbee Yoga Guide** Korean ??? system prompt.

### `ai/llm_service.py`
Async HTTP wrapper around Ollama `/api/chat`. Supports:
- `chat()` — single round-trip, returns full string.
- `stream_chat()` — async generator yielding token fragments.
- `chat_auto()` / `stream_auto()` — Ollama-first with transparent OpenAI fallback
  (soft deadline timeout before switching providers).

---

## API Quick Reference

### POST `/api/v1/match`

```bash
curl -X POST https://match.yogaman.club/api/v1/match \
  -H "Content-Type: application/json" \
  -d '{
    "healthFlags": [],
    "goals": ["Spinal_Mobility", "Stress_Relief"],
    "experienceLevel": "BEGINNER",
    "availableMinutes": 30,
    "topK": 5
  }'
```

**Response** — ranked poses array:
```json
[
  {
    "poseId": "...",
    "canonicalName": "Balasana",
    "commonName": "Child'\''s Pose",
    "difficultyRank": 1,
    "naturalDescription": "...",
    "score": 3.2,
    "blocked": false,
    "reason": null
  }
]
```

### GET `/api/v1/studios/nearby?lat=37.5&lng=127.0&radiusKm=5`
Returns studios within the given radius sorted by ascending distance (km).

### GET `/api/v1/instructors/{id}/jsonld`
Returns a Schema.org `Person` JSON-LD document for the instructor (E-E-A-T).

---

## Infrastructure

| Component | Detail |
|---|---|
| Spring Boot API | Port `19090`, Cloudflare proxy ? `match.yogaman.club` |
| PostgreSQL | Port `8879`, DB `yogadb`, 944 poses, 0 NULL descriptions |
| Ollama | `localhost:11434`, `mistral:latest` (7B Q4_K_M) |
| Elbee FastAPI | Port `8000` ? `elbee.yogaman.club` |

---

## Database Schema (key tables)

| Table | Rows | Notes |
|---|---|---|
| `poses` | 944 | Sanskrit names, difficulty ranks, `natural_description`, `schema_org_jsonld` |
| `benefits` | — | Weighted benefit tags linked to poses |
| `contraindications` | — | CRITICAL severity triggers Kill-Switch |
| `sessions` | 16 | Sample class sessions |
| `instructors` | 10 | Seeded with trust scores |
| `studios` | 10 | Seeded with lat/lng coordinates |

---

## Team

**AIBE5 Team 14** — Project 2
Repository: [prgrms-aibe-devcourse/AIBE5-Project2-TEAM14-bn](https://github.com/prgrms-aibe-devcourse/AIBE5-Project2-TEAM14-bn)
