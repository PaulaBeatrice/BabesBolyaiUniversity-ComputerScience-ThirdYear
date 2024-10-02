from copy import deepcopy

EPSILON = 'e'

class Grammar:
    def __init__(self, tabela_simboluri: dict[str, int], filename):
        with open(filename, "r") as f:
            txt = [x.strip() for x in f.readlines()]

        reg = []
        for stanga in txt:
            if stanga != '':
                reg.append(stanga)

        self.reguli = {}
        self.terminale = set()
        self.neterminale = set()
        self.numbered = {}
        self.start = "program" # declaram simbolul de start

        cnt = 1
        for rule in reg: # luam regulile de productie din fisier, si preluam terminale/ neterminale
            stanga, dreapta = rule.split(' -> ')
            if stanga not in self.reguli:
                self.reguli[stanga] = []
            dreapta = dreapta.split(" | ")
            for p in dreapta:
                z = []
                for w in p.split():
                    if w[0] == '"' and w[-1] == '"': # daca e terminal - keywords/operator/separator preia codul din tabela de simboluri
                        z.append(tabela_simboluri[w[1:-1]])
                    elif w in ("ID", "CONST"):
                        z.append(tabela_simboluri[w])
                    else:
                        z.append(w)
                self.reguli[stanga].append(tuple(z))
                self.numbered[(stanga, tuple(z))] = cnt # numeroteaza regulile de productie
                cnt += 1

            self.neterminale.add(stanga)

        for stanga in tabela_simboluri.values():
            self.terminale.add(stanga)
        self.terminale.add('e')

    def get_terminale(self):
        return self.terminale

    def get_neterminale(self):
        return self.neterminale

    def get_start(self):
        return self.start

    def get_number(self, result: str, rule: str):
        return self.numbered[(result, rule)]

    def first1(self) -> dict[str, set]:
        F = {}
        F[0] = {}
        for A in self.get_neterminale():
            F[0][A] = set()
            for regula in self.reguli[A]:
                if regula[0] in self.get_terminale():
                    F[0][A].add(regula[0])
        i = 0
        while True:
            i += 1
            F[i] = deepcopy(F[i - 1])

            for A in self.get_neterminale():
                for regula in self.reguli[A]:
                    X = regula[0]
                    if X in self.get_neterminale():
                        for a in F[i - 1][X]:
                            if a != EPSILON:
                                F[i][A].add(a)

            if F[i] == F[i - 1]:
                break

        return F[i - 1]

    def follow1(self) -> dict[str, set]:
        FOLL = {A: set() for A in self.get_neterminale()}
        FOLL[self.get_start()] = {"$"}
        while True:
            old_FOLL = deepcopy(FOLL)

            for A, reguli in self.reguli.items():
                for regula in reguli:
                    for i in range(len(regula)):
                        if regula[i] in self.get_neterminale():
                            B = regula[i]
                            beta = None
                            if i == len(regula) - 1:
                                beta = EPSILON
                            else:
                                beta = regula[i + 1]

                            if beta in self.get_neterminale():
                                for x in self.first1()[beta]:
                                    if x != EPSILON:
                                        FOLL[B].add(x)
                            else:
                                if beta != EPSILON:
                                    FOLL[B].add(beta)
                            if beta == EPSILON or (beta in self.get_neterminale() and EPSILON in self.first1()[beta]):
                                for x in FOLL[A]:
                                    FOLL[B].add(x)

            if old_FOLL == FOLL:
                break

        return FOLL

    def tabel_analiza(self) -> dict[str, dict]:
        table = {}
        for terminal in self.get_terminale():
            table[terminal] = {}
        for neterminal in self.get_neterminale():
            table[neterminal] = {}
        table['$'] = {}

        for k in table.keys():
            for terminal in self.get_terminale():
                table[k][terminal] = None
            table[k]['$'] = None

        for terminal in self.get_terminale():
            table[terminal][terminal] = "pop"
        table['$']['$'] = "acc"

        for A, reguli in self.reguli.items():
            for B in reguli:
                if B[0] == EPSILON:
                    for x in self.follow1()[A]:
                        if table[A][x] and table[A][x] != (B, self.get_number(A, B)):
                            print(f"CONFLICT ({A}, {x})")
                            return
                        table[A][x] = (B, self.get_number(A, B))
                elif B[0] in self.get_terminale():
                    if table[A][B[0]] and table[A][B[0]] != (B, self.get_number(A, B)):
                        print(f"CONFLICT ({A}, {B[0]})")
                        return
                    table[A][B[0]] = (B, self.get_number(A, B))
                elif B[0] in self.get_neterminale():
                    for x in self.first1()[B[0]]:
                        if table[A][x] and table[A][x] != (B, self.get_number(A, B)):
                            print(f"CONFLICT ({A}, {x})")
                            return
                        table[A][x] = (B, self.get_number(A, B))
        return table

    def verif(self, secv: str) -> list[int]:
        if not set(secv).issubset(self.get_terminale()):
            print("Fip-ul nu e valid!")
            return []

        table = self.tabel_analiza()
        if table is None:
            print("Tabelul are conflicte!")
            return []

        bandaDeIntrare = list(secv) + ["$"]
        stiva = [self.get_start()] + ["$"]
        bandaDeIesire = []
        sir_productii = []

        while True:
            if table[stiva[0]][bandaDeIntrare[0]] == "acc":
                print("Secventa e acceptata!\n" + "Sir productii: " + str(sir_productii))
                break
            elif table[stiva[0]][bandaDeIntrare[0]] == "pop":
                bandaDeIntrare = bandaDeIntrare[1:]
                stiva = stiva[1:]
            elif table[stiva[0]][bandaDeIntrare[0]] is None:
                print("Eroare - Secventa nu e acceptata!")
                break
            else:
                p, nr = table[stiva[0]][bandaDeIntrare[0]]
                sir_productii.append(nr)
                stiva = stiva[1:]
                if p[0] != EPSILON:
                    stiva = list(p) + stiva
                bandaDeIesire.append(nr)

        return bandaDeIesire