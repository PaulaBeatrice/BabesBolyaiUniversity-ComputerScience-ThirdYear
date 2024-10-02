% conditionare in functie de norma Cebisev
function a
warning('off')
for n = 10:15
    k = linspace(-1,1,n);
    t = -1 + k .* (2/n);
    V = vander(t);
    cond(V, inf)
end
endfunction
