noduri = [1, 1.8, 2.2];
nodevals = exp(noduri);
t = 1 : 0.01 : 3;

fi = Lagrange(noduri, nodevals, t);

plot(t, fi, 'color', 'red');
hold on;

resexp = exp(t);
plot(t, resexp, 'color', 'blue');
legend('Interpolare Lagrange', 'Functia aproximata')
hold off;
