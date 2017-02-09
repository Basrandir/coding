import sys

count = 0

for i in sys.argv:
    if count > 0:
        print(i + ':', i[::-1])

    count += 1
