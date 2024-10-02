noduri = [1 1.3 1.6 1.9]; %noduri de interpolare
m = 4;

labels = {};
for k = 1 : m
    t = 1 : 0.01 : 2; % intre 1 si 2 la pas de 0.01
    val = zeros(size(t));
    [~, c] = size(t);
    for i = 1 : c
       val(1, i) = evalPolinomFundamental(noduri, k, t(1, i));
    endfor
    p = val;
    labels = [labels num2str(k)];
    plot(t, p, 'color', rand(1,3))
    hold on;
endfor
legend(labels);
hold off;
