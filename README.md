# AIBE5-Project2-TEAM12-bn

## mini project (comic books rental service cli app)
```bash
a2485@maegbug-ui-MacBookPro cli-comics % export JAVA_HOME=/Library/Java/JavaVirt
ualMachines/temurin-21.jdk/Contents/Home && MYSQL_JAR=$(find ~/.gradle/caches -n
ame 'mysql-connector-j-8.0.33.jar' 2>/dev/null | head -1) && $JAVA_HOME/bin/java
 -cp "build/classes/java/main:$MYSQL_JAR" org.example.comics.Main
명령어: comic-list
번호 | 제목       | 권수 | 작가               | 상태     | 등록일
-------------------------------------------------------------
1    | 슬램덩크 완전판   | 31   | 이노우에 다케히코          | 대여가능       | 2026-03-06
3    | 원피스        | 1    | 오다 에이이치로           | 대여가능       | 2026-03-06
5    | 드래곤볼       | 42   | 토리야마 아키라           | 대여가능       | 2026-03-11
명령어: comic-detail 3
==== 만화책 상세 ====
번호   : 3
제목   : 원피스
권수   : 1
작가   : 오다 에이이치로
등록일 : 2026-03-06
명령어: member-list
번호 | 이름          | 전화번호          | 가입일
-------------------------------------------------------------
1    | 테스트회원      | test@test.com      | 2026-03-11
명령어: member-add
이름: 이 병 남       
전화번호: 01025441111
=> 회원 등록이 완료되었습니다. (id=2)
명령어: ㅡ member-list
번호 | 이름          | 전화번호          | 가입일
-------------------------------------------------------------
1    | 테스트회원      | test@test.com      | 2026-03-11
2    | 이병남       | 01025441111        | 2026-03-11
명령어: rent 11
인자가 부족합니다.
명령어: rent 1 1
=> 대여 완료: [대여id=2]
명령어: rent-list
대여id | 만화id | 회원id | 대여일     | 반납일
-----------------------------------------------
1      | 1      | 1      | 2026-03-11 | 2026-03-11
2      | 1      | 1      | 2026-03-11 | -         
명령어: return 1  
이미 반납 완료된 만화책입니다.
명령어: return 2
=> 반납 완료: 대여id=2
명령어:

```
