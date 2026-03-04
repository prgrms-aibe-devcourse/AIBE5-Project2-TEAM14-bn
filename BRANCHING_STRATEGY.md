# Branching Strategy

This repository follows a simple, Git-flow–inspired branching strategy to manage the development
of the **Comic Book Rental System** and subsequent feature work (e.g. SimpleDB thread support).

## Branches

1. **`main`**
   * Always contains production‑ready code.
   * Tags/releases are created off this branch.

2. **`develop`** *(optional)*
   * Integration branch for ongoing feature work.
   * Merges from feature branches land here first and are later merged into `main`.

3. **Feature branches**
   * Created from `develop` (or `main` if `develop` is not used).
   * Naming convention: `feature/<short-description>`.
   * Example initial branches:
     * `feature/comic-rental-core` – the original console‑based comic rental system.
     * `feature/simpledb-thread` – add support for SimpleDB threading features.
   * Work is committed to the feature branch and, when ready, a pull request is opened
to merge into `develop`/`main`.

4. **Hotfix branches**
   * Created from `main` to address urgent bugs in production.
   * Naming convention: `hotfix/<issue>`.
   * After fixing, merge back into both `main` and `develop`.

## Workflow

1. **Initialize**
   * Start with `main` containing the initial comic book rental system.
   * Optionally create `develop` to aggregate features.

2. **Develop features**
   * `git checkout -b feature/comic-rental-core` – scaffold the project, entities, DAOs, CLI logic.
   * Commit regularly; push the branch and open a pull request when ready.
   * Repeat for new capabilities such as `feature/simpledb-thread`.

3. **Integration and release**
   * Merge completed feature branches into `develop` (or `main`).
   * Run tests (if any) and ensure everything is stable.
   * Merge `develop` into `main` and tag a release when appropriate.

4. **Maintenance**
   * Create hotfix branches off `main` for critical fixes.
   * Always merge hotfixes back into `develop` to keep all branches up to date.

This lightweight branching model keeps the comic rental core isolated from experimental work
(and makes it easy to introduce the SimpleDB thread feature later) while maintaining a clean
history and straightforward release process.
