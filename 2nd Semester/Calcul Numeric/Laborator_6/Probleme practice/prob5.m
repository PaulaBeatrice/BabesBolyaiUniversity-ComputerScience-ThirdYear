x = [0 pi/6 pi/4 pi/3 pi/2];
y1 = [0 1/2 sqrt(2)/2 sqrt(3)/3 1];
y2 = [1 sqrt(3)/3 sqrt(2)/2 1/2 0];

xi = 5;

fi1 = Lagrange(x , y1 , xi)
fi2 = Lagrange(x , y2 , xi)
