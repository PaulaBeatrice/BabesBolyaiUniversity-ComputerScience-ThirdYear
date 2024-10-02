function fi=Lagrange(x,y,xi)
  % calculeaza polinomul de interpolare Lagrange
  % x,y - coordonatele nodurilor
  % xi - punctele in care se evalueaza polinomul
  [mu, nu] = size(xi);
  fi = zeros(mu, nu);
  y_length = length(y);
  for i = 1:y_length
      z = ones(mu, nu);
      for j = [1:i-1,i+1:y_length]
          z=z.*(xi-x(j))/(x(i)-x(j));
      endfor
      fi=fi+z*y(i);
  endfor
endfunction
