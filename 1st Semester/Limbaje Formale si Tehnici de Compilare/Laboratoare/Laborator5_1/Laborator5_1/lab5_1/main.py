import codecs
from gramatica import Gramatica

def main():
    menu = """
0. Exit
1. Verifica o secventa
>>> """
    cmd = input(menu)
    while cmd:
        if cmd == "1":
            deverificat = input("Introduceti secventa: ")

            g = Gramatica("ex1.txt")
            g.first1()
            print("FIRST(1)")
            for k in g.first:
                print(k, g.first[k])

            print()
            g.follow1()
            print("FOLLOW(1)")
            for k in g.follow:
                print(k, g.first[k])

            g.vfSecventa(deverificat)
            cmd = input(menu)
        elif cmd == "0":
            return
        else:
            print("Comanda invalida!!!")
            cmd = input(menu)


main()
