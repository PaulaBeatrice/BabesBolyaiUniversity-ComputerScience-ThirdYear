from gramatica import *

string_ts = """CONST     0
ID                       1
#include<iostream>       2
#include<cstring>        3
#include<cmath>          4
using                    5
namespace                6
std                      7
main                     8
int                      9
double                   10
struct                   11
if                       12
else                     13
while                    14
cin                      15
cout                     16
+                        17
-                        18
*                        19
%                        20
/                        21
.                        22
<                        23
<=                       24
>                        25
>=                       26
==                       27
!=                       28
<<                       29
>>                       30
=                        31
,                        32
;                        33
(                        34
)                        35
{                        36
}                        37
[                        38
]                        39"""

if __name__ == '__main__':
    dict_ts = {}
    for line in string_ts.split('\n'):
        x, y = line.split()
        dict_ts[x] = int(y)

    fip = []
    with open("files/fip_err.txt", "r") as f:
        for line in f.readlines():
            fip.append(int(line.split()[0]))

    gram = Grammar(dict_ts, "files/gramatica.txt")
    gram.verif(fip)
