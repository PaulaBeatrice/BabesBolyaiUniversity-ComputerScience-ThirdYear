from copy import deepcopy
import pandas as pd
EPSILON = 'e'


class Gramatica:
    def __init__(self, filename):
        with open(filename, "r") as f:
            fisier = [x.strip() for x in f.readlines()]

        self.reguliProductie = {}
        self.terminale = set()
        self.neterminale = set()
        self.start = None

        for line in fisier:  # parcurgem fiecare regula de productie, o adaugam, si extragem terminalele si neterminalele
            x, y = line.split(" -> ")
            self.neterminale.add(x)
            if not self.start:
                self.start = x  # initializam simbolul de start
            if x not in self.reguliProductie.keys():
                self.reguliProductie[x] = [y]
            else:
                self.reguliProductie[x].append(y)

            for l in y:
                if l.isupper():
                    self.neterminale.add(l)
                elif l.islower():
                    self.terminale.add(l)

        # initializam First si Follow pentru fiecare neterminal
        self.first = {x: set() for x in self.neterminale}
        self.follow = {x: set() for x in self.neterminale}

    def get_terminale(self):
        return self.terminale

    def get_neterminale(self):
        return self.neterminale

    def first1(self):
        """
        Calculeaza First1 pentru fiecare neterminal
        :return:
        """
        # Initializare dictionar pentru a stoca First1 pentru fiecare neterminal
        F = {}
        F[0] = {}
        for A in self.neterminale:
            # Initializare pentru neterminalul curent
            F[0][A] = set()
            for regula in self.reguliProductie[A]:  # pentru fiecare regula de productie a neterminalului curent
                if regula[0] in self.terminale:
                    F[0][A].add(regula[0])  # daca avem un terminal in partea dreapta a productiei, il adaugam in First1
        i = 0
        while True:
            i += 1
            # Copiem First1 de la iteratia anterioara pentru a verifica daca s-au facut modificari
            F[i] = deepcopy(F[i - 1])

            for A in self.neterminale:
                for regula in self.reguliProductie[A]:  # Pentru fiecare regula de productie a neterminalului curent
                    firstSymbol = regula[0]
                    if firstSymbol in self.neterminale:  # Daca primul simbol din partea dreapta este un neterminal
                        for a in F[i - 1][
                            firstSymbol]:  # Adaugam toate simbolurile diferita de epsilon din First1 ale neterminalului in First1-ul neterminalului curent
                            if a != EPSILON:
                                F[i][A].add(a)
            # Daca nu s-au produs schimbari intre iteratii, iesim din bucla
            if F[i] == F[i - 1]:
                break
        self.first = F[i - 1]

    def follow1(self):
        # initializare dictionar pentru a stoca Follow1 pentru fiecare neterminal
        FL = {A: set() for A in self.neterminale}
        FL[self.start] = {"$"}  # setam Follow1 pentru neterminalul de start
        while True:
            copyFL = deepcopy(FL)  # Salvam copia veche a Follow1 pentru a verifica schimbari

            for A, reguli in self.reguliProductie.items():  # parcurgem regulile de productie
                for regula in reguli:
                    for i in range(len(regula)):
                        if regula[i] in self.neterminale:
                            B = regula[i]
                            beta = None
                            # verificam daca suntem la sfarsitul regulii
                            if i == len(regula) - 1:
                                beta = EPSILON
                            else:
                                beta = regula[i + 1]

                            if beta in self.neterminale:  # saca beta este un neterminal
                                for x in self.first[beta]:  # adaugam First1(beta) in Follow1(B)
                                    if x != EPSILON:
                                        FL[B].add(x)
                            else:
                                if beta != EPSILON:  # saca beta este un terminal, il adaugam in Follow1(B)
                                    FL[B].add(beta)
                            if beta == EPSILON or (beta in self.neterminale and EPSILON in self.first[
                                beta]):  # daca beta este epsilon sau beta este un neterminal cu First1(beta) contine epsilon
                                for x in FL[A]:  # adaugam Follow1(A) in Follow1(B)
                                    FL[B].add(x)
            # daca nu s-au produs schimbari intre iteratii, iesim din bucla
            if copyFL == FL:
                break

        self.follow = FL

    def vfSecventa(self, secventaDeIntrare):
        """
        analiza sintactica corespunzatoare secventei de intrare
        :param secventaDeIntrare:
        :return: sirul productiilor, sau eroare, daca metoda nu se poate aplica(avem conflicte)
        """
        nrReguli = {}  # numerotam regulile de productie
        nr = 1
        for A, reguli in self.reguliProductie.items():
            for regula in reguli:
                nrReguli[(A, regula)] = nr
                nr += 1

        # initializam tabelul de analiza
        tabelAnaliza = {}
        for terminal in self.terminale:
            tabelAnaliza[terminal] = {}
        for neterminal in self.neterminale:
            tabelAnaliza[neterminal] = {}
        tabelAnaliza['$'] = {}

        for k in tabelAnaliza.keys():
            for terminal in self.terminale:
                tabelAnaliza[k][terminal] = None
            tabelAnaliza[k]['$'] = None

        # populam tabelul de analiza
        for terminal in self.terminale:
            tabelAnaliza[terminal][terminal] = "pop"
        tabelAnaliza['$']['$'] = "acc"

        # print(tabelAnaliza)


        for A, reguli in self.reguliProductie.items():
            for B in reguli:
                if B == EPSILON:
                    for nr in self.follow[A]:
                        if tabelAnaliza[A][nr] and tabelAnaliza[A][nr] != (B, nrReguli[(A, B)]):
                            print(
                                f"CONFLICT ({A}, {nr})")  # daca ajungem pe o pozitie deja ocupata, avem conflict si nu putem aplica metoda de analiza sintactica
                            return
                        tabelAnaliza[A][nr] = (B, nrReguli[(A, B)])
                elif B[0].islower():
                    if tabelAnaliza[A][B[0]] and tabelAnaliza[A][B[0]] != (B, nrReguli[(A, B)]):
                        print(f"CONFLICT ({A}, {B[0]})")
                        return
                    tabelAnaliza[A][B[0]] = (B, nrReguli[(A, B)])
                elif B[0].isupper():
                    for nr in self.first[B[0]]:
                        if tabelAnaliza[A][nr] and tabelAnaliza[A][nr] != (B, nrReguli[(A, B)]):
                            print(f"CONFLICT ({A}, {nr})")
                            return
                        tabelAnaliza[A][nr] = (B, nrReguli[(A, B)])

        z = pd.DataFrame(tabelAnaliza).T
        print(z)

        # initializam banda de intrare, stiva si banda de iesire
        bandaDeIntrare = secventaDeIntrare + "$"
        stiva = self.start + "$"
        bandaDeIesire = []
        while True:  # verificam daca secventa este acceptata
            if tabelAnaliza[stiva[0]][bandaDeIntrare[0]] == "acc":
                print("\nSecventa este acceptata!")
                if len(bandaDeIesire) != 0:
                    print("Sirul productiilor este:")
                    print(", ".join(map(str, bandaDeIesire)))
                break
            elif tabelAnaliza[stiva[0]][bandaDeIntrare[0]] == "pop":
                bandaDeIntrare = bandaDeIntrare[1:]
                stiva = stiva[1:]
            elif tabelAnaliza[stiva[0]][
                bandaDeIntrare[0]] == None:  # daca am ajuns pe o celula a tabelului care e goala => eroare
                print("EROARE - Secventa nu este acceptata")
                break
            else:
                p, nr = tabelAnaliza[stiva[0]][bandaDeIntrare[0]]
                stiva = stiva[1:]
                if p != EPSILON:
                    stiva = p + stiva
                bandaDeIesire.append(nr)
