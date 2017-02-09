import sys


def usage():
    print("Usage: baseC.py [INPUT BASE SYSTEM]::[OUTPUT BASE SYSTEM] [INTEGER]\n")
    print("[BASE SYSTEM] as either numerical digits or their corresponding names as defined below\n")
    print("Base Systems:")
    print("\tBase 2: binary")
    print("\tBase 10: decimal")
    print("\tBase 16: hex\n")
    print("Example:")
    print("baseC.py hex::decimal 4e")
    print("baseC.py 10::24 1337")


def toBase(inp, base):
    mod = inp % base
    digits = "0123456789abcdefghijklmnopqrstuvwxyz"

    if (inp >= base):
        return toBase(int(inp/base), base) + digits[mod]
    else:
        return digits[mod]


def toDecimal(inp, base):
    alp, dig = list(range(10, 36)), "abcdefghijklmnopqrstuvwxyz"
 
    length = len(str(inp)) - 1
    res = 0
    for i in str(inp):
        try:                                            # Checks to see if the digit is numerical
            res += int(i) * base ** length              # No changes needed
        except ValueError:
            res += alp[dig.index(i)] * base ** length   # If not, replaces alphabet character with corresponding numerical digit
        length -= 1

    return res


orig = sys.argv[1:]

try:
    init, to = orig[0].split("::")
    inp = int(orig[1])
except:
    usage()
    sys.exit(2)

try:
    init = int(init)
except ValueError:
    if (init == "binary"):
        init = 2
    elif (init == "decimal"):
        init = 10
    elif (init == "hex"):
        init = 16
    else:
        usage()
        sys.exit(2)

try:
    to = int(to)
except ValueError:
    if (to == "binary"):
        to = 2
    elif (to == "decimal"):
        to = 10
    elif (to == "hex"):
        to = 16
    else:
        usage()
        sys.exit(2)

if (init != 10):
    inp = toDecimal(inp, init)

inp = toBase(inp, to)

print(inp)
