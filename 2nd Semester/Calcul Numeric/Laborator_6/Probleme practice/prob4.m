function prob4()
  runge = @(x) 1/(1+x*x);
  noduri = [-5 -4 -3 -2 -1 0 1 2 3 4 5];
  [l, c] = size(noduri);
   for i = 1 : c
    valori(1,i) = runge(noduri(1,i));
  endfor

  t = -5 : 0.01 : 5;
  rez = zeros(size(t));
  fct = zeros(size(t));
  [l, c] = size(t);
  for i = 1 : c
    rez(1, i) = interpolareLagrange(t(1, i), noduri, valori);
    fct(1, i) = runge(t(1, i));
  end
  figure(1);
  plot(t, rez, 'red');
  hold on;
  plot(t, fct,'blue');


  bernstein = @(x) abs(x);
  noduri = [-1 0 1 ];
  [l, c] = size(noduri);
   for i = 1 : c
    valori(1,i) = bernstein(noduri(1,i));
  endfor

  t = -1 : 0.01 : 1;
  rez = zeros(size(t));
  fct = zeros(size(t));
  [l, c] = size(t);
  for i = 1 : c
    rez(1, i) = interpolareLagrange(t(1, i), noduri, valori);
    fct(1, i) = bernstein(t(1, i));
  end
  figure(2);
  plot(t, rez, 'red');
  hold on;
  plot(t, fct,'blue');
  hold off;


endfunction

function rez = interpolareLagrange(punct, noduri, valori)
  rez = 0;
  [l, c] = size(noduri);
  m = c;    x = punct;
  for k = 1 : m
    f = valori(1, k);
    l = 1;
    for j = 1 : m
      if j ~= k
        l = l * (x - noduri(1, j))/(noduri(1, k) - noduri(1, j));
      end
    end
    rez = rez + f * l;
  end
  endfunction
