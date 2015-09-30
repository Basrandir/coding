import sys

def usage():
    print ("Usage: baseC.py [BASE SYSTEM] [INTEGER]\n")
    print ("Base Systems:")
    print ("\tBase 2: binary")
    print ("\tBase 16: hex")
    
def toHex(x):
    mod = x % 16
    digits = "0123456789abcdef"

    if (x > 15):
        return toHex(int(x/16)) + digits[mod]
    else:
        return digits[mod]

def toBinary(x):
    mod = x % 2
    digits = "01"
    
    if (x > 1):
        return toBinary(int(x/2)) + digits[mod]
    else:
        return digits[mod]

orig = sys.argv[1:]

try:
    inp = int(orig[1])
except:
    usage()
    sys.exit(2)

if (orig[0] == "binary" or orig[0] == "-b"):
    print(toBinary(inp))
elif (orig[0] == "hex" or orig[0] == "-hx"):
    print(toHex(inp))
else:
    usage()
    sys.exit(2)
