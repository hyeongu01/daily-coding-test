---
name: daily-problem
description: 사용자의 풀이 기록(src/y*/m*/D*.java)을 분석해 약점·복습 주기를 반영한 프로그래머스/리트코드 문제를 하루 한 문제 추천하고, 풀이 성공/실패 결과를 프로필에 반영한다. "오늘의 문제", "문제 추천", "문제 하나 줘", "다 풀었어", "못 풀었어", "패스" 같은 말에 사용.
---

# daily-problem — 개인화 코딩테스트 문제 추천

풀이 기록을 `data/profile.json` 에 분석·저장해 두고, 추천할 때마다 그 프로필을 읽어 문제를 고르며, 풀이 결과가 나오면 프로필을 갱신한다.

- 스크립트: `.claude/skills/daily-problem/scripts/profile.py` (표준 라이브러리만 사용)
- 상태 파일: `.claude/skills/daily-problem/data/profile.json`
- 실행은 항상 `python -I .claude/skills/daily-problem/scripts/profile.py <명령>` (저장소 루트에서)

## 프로필이 담는 것
- `problems`: 문제별 `id`(`leetcode:<slug>` / `programmers:<lesson번호>`), 제목, URL, `tags`, `difficulty`, `status`(solved=품 / gave_up=포기 / solving=푸는중), 날짜별 `history`(같은 문제를 나중에 또 풀면 새 항목이 같은 문제에 쌓인다. 시도 횟수 필드는 없다)
- `topics`: 태그별 solved/gave_up/solving 개수, 마지막으로 푼 날짜 (problems 에서 자동 재계산)
- `recommendations`: 지금까지 추천한 문제와 결과(`outcome`) — 중복 추천 방지

태그 표준어: `array string hash-map hash-set two-pointers sliding-window stack monotonic-stack queue heap linked-list tree recursion math prefix-sum matrix greedy simulation sorting binary-search dp graph backtracking bit-manipulation trie union-find` (새 주제가 필요하면 kebab-case 로 추가).

## 흐름

### 0. 최초 1회 초기화 (profile.json 이 없을 때만)
1. `profile.py init` — `src/` 전체를 스캔해 프로필 생성. 풀이 파일의 javadoc 링크로 id 를 만들고, 링크가 없는 파일은 파일명을 kebab-case 로 바꿔 `leetcode:<slug>` 로 추정한다. README/CLAUDE.md 의 "미해결 문제" 섹션에 링크된 파일은 `solving`(푸는중)으로 표시된다.
2. 자동 태그는 거칠다(예: `String` 타입만 써도 string 이 붙음). `profile.py list` 로 전체를 보고 **문제 내용을 아는 대로 태그/난이도를 교정**한다. 교정 결과를 JSON(`{id: {"tags": [...], "difficulty": "easy|medium|hard"}}`)으로 scratchpad 에 쓰고 `profile.py retag --file <경로>` 로 반영. 모르는 문제는 건드리지 않는다.
3. 사용자에게 초기 분석 요약(풀이 수, 강/약 태그, 미경험 영역)을 짧게 보여 준다.

### 1. 문제 추천 ("오늘의 문제 줘")
1. `profile.py brief` 실행. (내부에서 `sync` 를 먼저 수행해, 마지막 분석 이후 새로 커밋된 풀이 파일을 `solved` 로 자동 반영한다.)
2. 아래 슬롯 중 하나를 고른다.
   - **복습 (약 25%)**: `retry_queue` 가 비어 있지 않으면 가장 오래 방치된 것. 같은 문제를 다시 주거나 같은 패턴의 변형을 준다.
   - **약점 보강 (약 50%)**: `weak_topics_top` 상위 태그 중 하나. 풀이 수가 적거나 실패율이 높거나 오래 안 건드린 주제.
   - **신규/확장 (약 25%)**: `known_tags` 에 없는 미경험 주제(dp, graph, binary-search, backtracking, trie, union-find 등) 또는 최근 강한 주제의 한 단계 위 난이도.
3. **목적은 사용자의 코딩테스트 역량을 서서히 끌어올리는 것**이다. 사용자가 '풀 만한' 문제를 주고, 난이도는 천천히 올린다.
   - 기본은 해당 주제에서 이미 푼 난이도와 같거나 **최대 한 단계 위**. 한 번에 두 단계 이상 올리지 않는다. 풀이 수가 적은 주제는 같은 난이도에서 먼저 익숙해지게 한다.
   - 같은 난이도를 쉽게(짧은 시간, 막힘 없이) 풀었다면 그때 한 단계 올린다. 오래 걸렸거나 고민이 컸다면 같은 난이도를 유지한다. 포기가 있었으면 한 단계 낮춘다.
   - hard 는 medium 을 여러 번 안정적으로 푼 주제에서만 가끔 준다.
   - 복습 슬롯에서 `gave_up` 문제가 hard 이거나 사용자 수준보다 높으면 그대로 다시 주지 말고, 같은 패턴의 더 쉬운 변형/선행 문제로 대체한다.
4. 플랫폼은 `platform_counts` 가 적은 쪽을 가끔 섞되(프로그래머스 비중이 낮음), 최근에 같은 플랫폼만 준 경우 번갈아 준다.
5. 문제 선정: 실제로 존재하는 문제여야 한다. 제목·번호·URL 이 확실하지 않으면 WebSearch 로 확인하고, 확인 못 하면 확실히 아는 문제로 바꾼다. **`exclude_ids` 에 있는 id 는 절대 추천하지 않는다.**
6. 사용자에게 보여 줄 것 (간결하게): 플랫폼·번호/제목·난이도·URL, 추천 이유 한 줄(예: "linked-list 풀이 1개뿐이고 58일 지남"), 주제 힌트는 정답을 알려 주지 않는 선에서.
7. **풀이 파일 스캐폴드 생성** (아래 "스캐폴드 규칙" 참고): 추천과 동시에 `src/y{yyyy}/m{MM}/D{dd}_{ProblemName}.java` 를 만든다. 사용자가 문제를 풀기 전에 파일을 먼저 만들어 줘서, 바로 풀이만 채우면 된다.
8. `profile.py log-rec --id <id> --title "<제목>" --tags a,b --difficulty medium --reason "<이유>"` 로 추천 이력 기록.

### 스캐폴드 규칙
- 경로/이름: 오늘 날짜 기준 `src/y{yyyy}/m{MM}/D{dd}_{EnglishPascalCaseName}.java`. 프로그래머스 한글 제목도 영문 PascalCase 로 짓는다(예: 명예의 전당 → `D12_HallOfFame`). 같은 이름 파일이 이미 있으면 덮어쓰지 말고 알린다. 폴더가 없으면 만든다.
- 형식은 `README.md` 의 "파일 형식"과 `utils.TestRunner`/`TestCase` 사용법을 따른다. 먼저 기존 파일 1~2개(리트코드는 `D14_LargestRectangleInHistogram`, 프로그래머스는 `D12_HallOfFame`)를 참고해 스타일을 맞춘다.
- javadoc 에 **문제 링크 필수**(sync 가 이 링크로 id 를 만든다). 그 아래 `키워드:` 줄에 추천 태그를 쓴다. 정답 알고리즘을 직접 알려 주는 표현은 쓰지 않고 주제 수준으로만 적는다.
- `main` 은 문제 예시 입출력을 `TestCase` 로 넣어 `TestRunner.run` 을 호출한다. 예시는 문제 페이지(필요하면 WebFetch/WebSearch)에서 확인한 값만 쓰고, 확인 못 하면 비워 두고 사용자에게 알린다.
- 풀이 메서드는 시그니처만 두고 본문은 비운다(컴파일되도록 기본값 반환). 정답 코드는 쓰지 않는다.
- **반드시 파일 안에 `// TODO(daily-problem): 풀이 완료 후 이 줄을 삭제하세요` 한 줄을 넣는다.** 이 마커가 남아 있는 파일은 `sync` 가 풀이로 인식하지 않는다. 사용자가 풀이를 끝내고 마커를 지우면 다음 `brief`/`sync` 때 자동으로 `solved` 반영된다.
- 사용자가 패스하면 방금 만든 스캐폴드 파일은 삭제한다(아직 마커가 있는 경우에만).

### 2. 결과 반영
- **푼 경우 (스캐폴드의 `TODO(daily-problem)` 마커를 지우고 풀이를 채운 경우)**: 별도 작업 불필요. 다음 `brief` 때 `sync` 가 자동 반영한다. 즉시 반영하려면 `profile.py sync`. 단, 풀이 시간/난이도 체감을 말해 주면 `record` 로 남긴다.
- **못 푼 경우 / 포기**: `profile.py record --result gave_up --id <id> --title "<제목>" --url <URL> --tags a,b --difficulty medium --note "막힌 지점"` 
  (id 를 모르면 `--platform leetcode --slug <slug>`). gave_up 은 복습 큐에 들어가고 해당 태그의 우선순위를 올린다.
- **패스/스킵**: `--result skipped`. 상태는 바꾸지 않고 이력만 남긴다.
- 추천 후 사용자가 "풀었다"고만 말하고 파일을 아직 안 만든 경우에도 `record --result solved` 를 쓸 수 있다. 이후 파일이 생겨도 같은 id 로 병합된다.
- 사용자가 막힌 이유(개념 부족, 구현 실수, 시간 초과 등)를 말하면 `--note` 에 남기고, 다음 추천에서 약점 판단에 쓴다.

### 3. 풀이 파일 규칙 (sync 가 의존하는 것)
파일은 `src/y{yyyy}/m{MM}/D{dd}_{ProblemName}.java`, 클래스 위 javadoc 에 문제 링크(`https://leetcode.com/problems/<slug>` 또는 `https://school.programmers.co.kr/learn/courses/30/lessons/<번호>`)와 가능하면 `자료구조:` / `알고리즘:` / `키워드:` 줄을 둔다. 이 줄이 있으면 태그 자동 추정이 정확해진다. 새 풀이 파일은 `README.md` 의 파일 형식을 따른다.

## 주의
- `profile.json` 은 사용자의 학습 상태다. `init --force` 는 추천 이력까지 지우므로 사용자가 요청할 때만 쓴다.
- 직접 JSON 을 편집하지 말고 스크립트 명령을 통해서만 갱신한다.
- 추천은 하루 한 문제가 기본이다. 사용자가 다른 문제를 요구하면(= 패스) `skipped` 를 기록하고 다시 고른다.
