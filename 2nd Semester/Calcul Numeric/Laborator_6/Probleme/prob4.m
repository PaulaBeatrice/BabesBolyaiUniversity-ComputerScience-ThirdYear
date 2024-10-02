f = @exp;
x = 1.2;
m = 3;
nodes = [1.1 1.4 1.6];

mNodes = nodes(1 : m);
val = Lagrange(mNodes, f(mNodes), x)
y = f(1.2)
