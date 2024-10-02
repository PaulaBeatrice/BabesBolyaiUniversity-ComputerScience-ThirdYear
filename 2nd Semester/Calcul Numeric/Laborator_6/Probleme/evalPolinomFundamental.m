function [val] = evalPolinomFundamental(noduri, k, x)
    % evalueaza valoarea polinomului fundamental
    [~, m] = size(noduri);
    u = 1; d = 1;
    for j = [1:k-1,k+1:m]
       if j ~= k
          u = u * (x - noduri(1, j));
          d = d * (noduri(1, k) - noduri(1, j));
       endif
    endfor
    val = u / d;
endfunction
