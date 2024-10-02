% f(x) = 2^x
x = [0 1 3 4];
y = [1 2 8 16];

xi = 2;

% calculeaza polinomul de interpolare Lagrange
% x,y - coordonatele nodurilor
% xi - punctele in care se evalueaza polinomul

[mu, nu] = size(xi)   % nr de randuri si nr de coloane
fi = zeros(mu, nu);   % matricea fi
y_length = length(y);
for i = 1:y_length   %parcurg toate punctele de interpolare
    z = ones(mu, nu);  % calculez polinomul Lagrange
    for j = [1:i-1,i+1:y_length] % fara punctul i
        z=z.*(xi-x(j))/(x(i)-x(j));
    endfor
    fi=fi+z*y(i) % adaug pol Lang la matricea fi
endfor

disp(fi);
