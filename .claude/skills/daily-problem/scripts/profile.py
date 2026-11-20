#!/usr/bin/env python3
"""daily-problem 스킬의 풀이 프로필 관리 도구.

서브커맨드
  init   [--force]   src/ 의 풀이 파일을 전부 분석해 data/profile.json 을 최초 생성
  sync               init 이후 새로 추가된 풀이 파일을 profile 에 반영 (= solved 처리)
  record             풀이 상태(solved=품 / gave_up=포기 / solving=푸는중, skipped=패스 이력만)를 직접 기록
  log-rec            추천한 문제를 이력에 기록 (중복 추천 방지용)
  brief              추천에 쓸 요약(JSON)을 stdout 으로 출력 (sync 를 자동 수행)

표준 라이브러리만 사용. 모든 출력은 UTF-8.
"""
import argparse
import json
import re
import sys
from datetime import date, datetime
from pathlib import Path

sys.stdout.reconfigure(encoding="utf-8")

SKILL_DIR = Path(__file__).resolve().parent.parent
REPO = SKILL_DIR.parent.parent.parent
SRC = REPO / "src"
PROFILE = SKILL_DIR / "data" / "profile.json"

# 코드/주석에서 태그를 추정하는 규칙 (정규식 -> 정규 태그명)
CODE_RULES = [
    (r"\bPriorityQueue\b|\bMinHeap\b|\bMaxHeap\b|우선순위\s*큐|힙|\bheap\b", "heap"),
    (r"\bDeque\b|\bArrayDeque\b|모노토닉|모노로틱|monotonic", "monotonic-stack/deque"),
    (r"\bStack\b|스택", "stack"),
    (r"\bQueue\b|큐", "queue"),
    (r"\bListNode\b|linked\s*list|연결\s*리스트", "linked-list"),
    (r"\bTreeNode\b|이진\s*트리|binary\s*tree|\btree\b|트리", "tree"),
    (r"\bHashMap\b|\bMap<|해시맵", "hash-map"),
    (r"\bHashSet\b|\bSet<|해시셋", "hash-set"),
    (r"슬라이딩\s*윈도우|sliding\s*window", "sliding-window"),
    (r"two\s*pointer|투\s*포인터|투포인터", "two-pointers"),
    (r"\bdfs\b|\bbfs\b|깊이\s*우선|너비\s*우선", "graph-traversal"),
    (r"재귀|recursion|recursive", "recursion"),
    (r"\bdp\b|dynamic\s*programming|동적\s*계획", "dp"),
    (r"binary\s*search|이분\s*탐색|이진\s*탐색", "binary-search"),
    (r"prefix\s*sum|누적\s*합", "prefix-sum"),
    (r"\bgreedy\b|그리디|탐욕", "greedy"),
    (r"\bgcd\b|최대공약수|소수|\bmath\b", "math"),
    (r"StringBuilder|\.charAt\(|문자열|\bstring\b", "string"),
    (r"\bint\[\]|\barray\b|배열", "array"),
    (r"\bsort\(|\bArrays\.sort|정렬", "sorting"),
    (r"backtrack|백트래킹", "backtracking"),
    (r"bit\s*manip|비트", "bit-manipulation"),
    (r"\bmatrix\b|\bint\[\]\[\]|행렬", "matrix"),
    (r"\bsimulation\b|시뮬레이션", "simulation"),
]
# 약한(흔한) 태그: 라벨이 없을 때 강한 태그가 있으면 제외
WEAK_TAGS = {"array", "string", "sorting", "math", "queue", "tree"}

LABEL_RE = re.compile(r"(자료구조|알고리즘|키워드)\s*:\s*(.+)")
FILE_RE = re.compile(r"D(\d{2})_(.+)\.java$")


def today():
    return date.today().isoformat()


def load():
    if not PROFILE.exists():
        sys.exit("profile.json 이 없습니다. 먼저 `python profile.py init` 을 실행하세요.")
    p = json.loads(PROFILE.read_text(encoding="utf-8"))
    migrate(p)
    return p


STATUS_RENAME = {"failed": "gave_up", "unsolved": "solving"}


def migrate(p):
    """v1 -> v2: attempts 제거, 상태명 변경(failed->gave_up, unsolved->solving), 같은 날 중복 이력 병합."""
    if p.get("version", 1) >= 2:
        return
    for e in p["problems"].values():
        e.pop("attempts", None)
        e["status"] = STATUS_RENAME.get(e["status"], e["status"])
        merged = []
        for h in e["history"]:
            h["result"] = STATUS_RENAME.get(h["result"], h["result"])
            if merged and merged[-1]["date"] == h["date"] and merged[-1]["result"] == h["result"]:
                merged[-1].update(h)
            else:
                merged.append(h)
        e["history"] = merged
    for r in p["recommendations"]:
        if r.get("outcome") in STATUS_RENAME:
            r["outcome"] = STATUS_RENAME[r["outcome"]]
    p["version"] = 2


def save(p):
    p["updated_at"] = datetime.now().isoformat(timespec="seconds")
    recompute_topics(p)
    PROFILE.parent.mkdir(parents=True, exist_ok=True)
    PROFILE.write_text(json.dumps(p, ensure_ascii=False, indent=2), encoding="utf-8")


def parse_link(text):
    m = re.search(r"https?://(?:www\.)?leetcode\.com/problems/([\w-]+)", text)
    if m:
        return "leetcode", m.group(1), m.group(0)
    m = re.search(r"https?://school\.programmers\.co\.kr/learn/courses/\d+/lessons/(\d+)", text)
    if m:
        return "programmers", m.group(1), m.group(0)
    return None, None, None


def infer_tags(text):
    tags = []
    # 1) '자료구조/알고리즘/키워드:' 라벨 줄은 신뢰도가 높다
    labeled = " ".join(m.group(2) for m in LABEL_RE.finditer(text))
    for pat, tag in CODE_RULES:
        if re.search(pat, labeled, re.I) and tag not in tags:
            tags.append(tag)
    # 2) 라벨이 없으면 코드 전체에서 추정
    if not tags:
        for pat, tag in CODE_RULES:
            if re.search(pat, text, re.I) and tag not in tags:
                tags.append(tag)
        strong = [t for t in tags if t not in WEAK_TAGS]
        tags = strong[:4] if strong else tags[:2]
    return tags


def analyze_file(path):
    m = FILE_RE.search(path.name)
    if not m:
        return None
    parts = path.relative_to(SRC).parts
    ym = re.match(r"y(\d{4})", parts[0]) if parts else None
    mm = re.match(r"m(\d{2})", parts[1]) if len(parts) > 1 else None
    if not (ym and mm):
        return None
    text = path.read_text(encoding="utf-8", errors="ignore")
    platform, pid, url = parse_link(text)
    title = re.sub(r"(?<!^)(?=[A-Z0-9])", " ", m.group(2)).strip()
    inferred = False
    if not platform:
        # 링크가 없는 파일: 파일명을 kebab-case 로 바꿔 leetcode 슬러그로 추정
        platform, url, inferred = "leetcode", None, True
        pid = re.sub(r"[^a-z0-9]+", "-", title.lower()).strip("-")
    return {
        "id": f"{platform}:{pid}",
        "platform": platform,
        "title": title,
        "url": url,
        "solved_date": f"{ym.group(1)}-{mm.group(1)}-{m.group(1)}",
        "file": path.relative_to(REPO).as_posix(),
        "tags": infer_tags(text),
        "inferred_id": inferred,
    }


def unsolved_ids_from_claude_md():
    """README.md / CLAUDE.md 의 '미해결 문제' 섹션 파일 링크 -> 해당 파일의 id."""
    out = set()
    for name in ("README.md", "CLAUDE.md"):
        f = REPO / name
        if not f.exists():
            continue
        md = f.read_text(encoding="utf-8")
        sec = re.search(r"##\s*미해결 문제(.*?)(?:\n##\s|\Z)", md, re.S)
        if not sec:
            continue
        for link in re.findall(r"\]\(([^)]+\.java)\)", sec.group(1)):
            fp = REPO / link
            if fp.exists():
                info = analyze_file(fp)
                if info:
                    out.add(info["id"])
    return out


PENDING_MARKER = "TODO(daily-problem)"


def scan():
    """풀이 파일 분석. 추천 시 만든 스캐폴드(마커가 남은 파일)는 아직 안 푼 것이므로 제외한다."""
    res = []
    for f in sorted(SRC.glob("y*/m*/D*.java")):
        if PENDING_MARKER in f.read_text(encoding="utf-8", errors="ignore"):
            continue
        info = analyze_file(f)
        if info:
            res.append(info)
    return res


def new_entry(info, status):
    return {
        "id": info["id"], "platform": info["platform"], "title": info["title"],
        "url": info["url"], "tags": info["tags"], "difficulty": None,
        "status": status,  # solved(품) | gave_up(포기) | solving(푸는중)
        "first_date": info["solved_date"], "last_date": info["solved_date"],
        "file": info.get("file"),
        "history": [{"date": info["solved_date"], "result": status}],
    }


def cmd_init(a):
    if PROFILE.exists() and not a.force:
        sys.exit("이미 profile.json 이 있습니다. 다시 만들려면 --force (기존 기록/추천 이력은 삭제됨).")
    unsolved = unsolved_ids_from_claude_md()
    problems = {}
    infos = scan()
    for info in infos:
        status = "solving" if info["id"] in unsolved else "solved"
        e = problems.get(info["id"])
        if e:  # 같은 문제를 여러 번 푼 경우
            e["last_date"] = max(e["last_date"], info["solved_date"])
            e["history"].append({"date": info["solved_date"], "result": status})
            e["tags"] = sorted(set(e["tags"]) | set(info["tags"]))
        else:
            problems[info["id"]] = new_entry(info, status)
    p = {"version": 2, "initialized_at": datetime.now().isoformat(timespec="seconds"),
         "known_files": [i["file"] for i in infos], "problems": problems,
         "recommendations": [], "topics": {}}
    save(p)
    print(f"초기화 완료: 문제 {len(problems)}개, 태그 {len(p['topics'])}종 -> {PROFILE}")


def cmd_sync(a=None, quiet=False):
    p = load()
    known = set(p["known_files"])
    added = []
    for info in scan():
        if info["file"] in known:
            continue
        p["known_files"].append(info["file"])
        e = p["problems"].get(info["id"])
        if e:
            e["status"] = "solved"
            e["last_date"] = max(e["last_date"], info["solved_date"])
            e["history"].append({"date": info["solved_date"], "result": "solved"})
            e["tags"] = sorted(set(e["tags"]) | set(info["tags"]))
            e["file"] = info["file"]
        else:
            p["problems"][info["id"]] = new_entry(info, "solved")
        for r in p["recommendations"]:
            if r["id"] == info["id"] and r["outcome"] is None:
                r["outcome"] = "solved"
        added.append(info["id"])
    save(p)
    if not quiet:
        print(f"신규 풀이 {len(added)}개 반영: {added}")
    return p


def cmd_record(a):
    p = load()
    pid = a.id or f"{a.platform}:{a.slug}"
    tags = [t.strip() for t in (a.tags or "").split(",") if t.strip()]
    d = a.date or today()
    e = p["problems"].get(pid)
    if not e:
        e = {"id": pid, "platform": a.platform or pid.split(":")[0], "title": a.title or pid,
             "url": a.url, "tags": tags, "difficulty": a.difficulty, "status": "solving",
             "first_date": d, "last_date": d, "file": None, "history": []}
        p["problems"][pid] = e
    if a.result != "skipped":
        e["status"] = a.result
    e["last_date"] = d
    if tags:
        e["tags"] = sorted(set(e["tags"]) | set(tags))
    if a.difficulty:
        e["difficulty"] = a.difficulty
    h = {"date": d, "result": a.result}
    if a.minutes is not None:
        h["minutes"] = a.minutes
    if a.note:
        h["note"] = a.note
    last = e["history"][-1] if e["history"] else None
    if last and last["date"] == d and last["result"] == a.result:
        last.update(h)  # 같은 날 같은 결과(sync 가 먼저 남긴 항목 등)는 새 이력 대신 보강
    else:
        e["history"].append(h)
    for r in p["recommendations"]:
        if r["id"] == pid and r["outcome"] is None:
            r["outcome"] = a.result
    save(p)
    print(f"기록: {pid} -> {a.result} (풀이 이력 {len(e['history'])}건)")


def cmd_retag(a):
    """Claude 가 문제 내용을 보고 교정한 태그/난이도를 반영. --file 은 {id: {"tags": [...], "difficulty": "easy"}} JSON."""
    p = load()
    data = json.loads(Path(a.file).read_text(encoding="utf-8"))
    n = 0
    for pid, v in data.items():
        e = p["problems"].get(pid)
        if not e:
            continue
        if "tags" in v:
            e["tags"] = v["tags"]
        if v.get("difficulty"):
            e["difficulty"] = v["difficulty"]
        if v.get("url"):
            e["url"] = v["url"]
        n += 1
    p["retagged"] = True
    save(p)
    print(f"{n}개 문제 태그/난이도 갱신")


def cmd_list(a):
    """태그 교정용: 문제 id / 제목 / 현재 태그를 한 줄씩 출력."""
    p = load()
    for e in p["problems"].values():
        print(f'{e["id"]}\t{e["title"]}\t{",".join(e["tags"])}\t{e["difficulty"] or "-"}')


def cmd_log_rec(a):
    p = load()
    p["recommendations"].append({
        "id": a.id, "title": a.title, "date": today(), "reason": a.reason,
        "tags": [t for t in (a.tags or "").split(",") if t],
        "difficulty": a.difficulty, "outcome": None})
    save(p)
    print(f"추천 이력 기록: {a.id}")


def recompute_topics(p):
    topics = {}
    for e in p["problems"].values():
        for t in e["tags"]:
            s = topics.setdefault(t, {"solved": 0, "gave_up": 0, "solving": 0, "last_date": "0000-00-00"})
            s[e["status"]] += 1
            s["last_date"] = max(s["last_date"], e["last_date"])
    p["topics"] = topics


def days_since(d):
    return (date.today() - date.fromisoformat(d)).days


def cmd_brief(a):
    p = cmd_sync(quiet=True)
    probs = list(p["problems"].values())
    ranked = []
    for t, s in p["topics"].items():
        total = s["solved"] + s["gave_up"] + s["solving"]
        fail_rate = (s["gave_up"] + s["solving"]) / total if total else 0
        stale = days_since(s["last_date"]) if s["last_date"] != "0000-00-00" else 999
        # 적게 풀었고 + 실패율 높고 + 오래 안 건드린 주제일수록 높은 우선순위
        priority = round(2 / (1 + s["solved"]) + fail_rate * 3 + min(stale, 60) / 30, 2)
        ranked.append({"tag": t, **s, "days_since_last": stale,
                       "fail_rate": round(fail_rate, 2), "priority": priority})
    weak = sorted(ranked, key=lambda x: -x["priority"])
    strong = sorted(ranked, key=lambda x: -x["solved"])

    retry = [{"id": e["id"], "title": e["title"], "url": e["url"], "status": e["status"],
              "days_since_last": days_since(e["last_date"]), "tags": e["tags"]}
             for e in probs if e["status"] in ("gave_up", "solving")]
    retry.sort(key=lambda x: -x["days_since_last"])

    recent = [r["id"] for r in p["recommendations"] if days_since(r["date"]) <= 30]
    exclude = sorted({e["id"] for e in probs if e["status"] == "solved"} | set(recent))

    out = {
        "today": today(),
        "stats": {"solved": sum(1 for e in probs if e["status"] == "solved"),
                  "gave_up_or_solving": len(retry),
                  "last_activity": max((e["last_date"] for e in probs), default=None)},
        "weak_topics_top": weak[:6],
        "strong_topics": strong[:4],
        "retry_queue": retry[:5],
        "pending_recommendations": [r for r in p["recommendations"] if r["outcome"] is None][-3:],
        "exclude_ids": exclude,
        "platform_counts": {pl: sum(1 for e in probs if e["platform"] == pl)
                            for pl in ("leetcode", "programmers")},
        "difficulty_counts": {d: sum(1 for e in probs if e["difficulty"] == d)
                              for d in ("easy", "medium", "hard")},
        "known_tags": sorted(p["topics"]),
    }
    print(json.dumps(out, ensure_ascii=False, indent=2))


def main():
    ap = argparse.ArgumentParser()
    sp = ap.add_subparsers(dest="cmd", required=True)
    i = sp.add_parser("init")
    i.add_argument("--force", action="store_true")
    i.set_defaults(f=cmd_init)
    s = sp.add_parser("sync")
    s.set_defaults(f=cmd_sync)
    b = sp.add_parser("brief")
    b.set_defaults(f=cmd_brief)
    r = sp.add_parser("record")
    r.add_argument("--result", required=True, choices=["solved", "gave_up", "solving", "skipped"])
    r.add_argument("--id")
    r.add_argument("--platform", choices=["leetcode", "programmers"])
    r.add_argument("--slug")
    r.add_argument("--title")
    r.add_argument("--url")
    r.add_argument("--tags")
    r.add_argument("--difficulty", choices=["easy", "medium", "hard"])
    r.add_argument("--minutes", type=int)
    r.add_argument("--note")
    r.add_argument("--date")
    r.set_defaults(f=cmd_record)
    t = sp.add_parser("retag")
    t.add_argument("--file", required=True)
    t.set_defaults(f=cmd_retag)
    ls = sp.add_parser("list")
    ls.set_defaults(f=cmd_list)
    l = sp.add_parser("log-rec")
    l.add_argument("--id", required=True)
    l.add_argument("--title", required=True)
    l.add_argument("--reason")
    l.add_argument("--tags")
    l.add_argument("--difficulty")
    l.set_defaults(f=cmd_log_rec)
    a = ap.parse_args()
    a.f(a)


if __name__ == "__main__":
    main()
