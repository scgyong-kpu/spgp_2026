import argparse
import random

# 이 스크립트는 TapTu 의 note file 에 넣을 "N lane millis" 줄을 간단히 만들어 준다.
# 인자를 주지 않고 실행하면 아래 기본값으로 생성한다.
#   python3 mknote.py
#
# bpm/start/end 를 직접 주고 싶으면 위치 인자로 넘긴다.
#   python3 mknote.py 120 10000 60000
#
# bpm 을 0 으로 주면 실제 note 를 만들지 않고 사용법만 출력한다.
#   python3 mknote.py 0
DEFAULT_BPM = 117
DEFAULT_START = 8510
DEFAULT_END = 70000


def main():
  parser = argparse.ArgumentParser(
    description='Generate random TapTu note lines from bpm/start/end.',
  )
  # nargs='?' 는 해당 인자가 생략될 수 있다는 뜻이다.
  # 생략되면 default 값이 들어가므로, 명령행 인자 없이도 바로 실행할 수 있다.
  parser.add_argument('bpm', nargs='?', type=float, default=DEFAULT_BPM)
  parser.add_argument('start', nargs='?', type=float, default=DEFAULT_START)
  parser.add_argument('end', nargs='?', type=float, default=DEFAULT_END)
  args = parser.parse_args()

  if args.bpm == 0:
    parser.print_usage()
    return

  # 60000ms 는 1분이다.
  # 60000 / bpm 은 한 박자의 길이(ms)이고,
  # 여기서는 두 박자마다 기본 note 하나를 찍기 위해 * 2 를 한다.
  mspb = 60000 / args.bpm * 2
  time = args.start
  while time < args.end:
    printNote(time)

    # 기본 note 사이가 너무 비어 보이지 않도록,
    # 절반 지점에도 추가 note 를 넣을지 50% 확률로 결정한다.
    split = random.randrange(0, 2) == 0
    if split:
      printNote(time + mspb / 2)
    time += mspb

def printNote(t):
  # note file 은 "N pret millis" 형식이다.
  # pret/lane 은 0~4 중 하나를 무작위로 고른다.
  # time 은 정수 millisecond 로 출력해야 하므로 round() 로 반올림한다.
  msec = round(t)
  lane = random.randrange(0, 5)
  print(f'N {lane} {msec}')

main()
