f = @(x) exp(x) .* cos(x.^2);

% Definim polinoamele lui Cebisev Tk(k, x)
Tk = @(k, x) cos(k * acos(x));

% precizia dorita
toleranta = 1e-6;

k = 0;
% Calculam coeficientul pentru k=0
c0 = (2 - (k==0)) / pi * integral(@(x) f(x) .* Tk(k, x) ./ sqrt(1 - x.^2), -1, 1);
integral_value_prev = c0; 

k = 2;
c2 = (2 - (k==0)) / pi * integral(@(x) f(x) .* Tk(k, x) ./ sqrt(1 - x.^2), -1, 1);
integral_value = integral_value_prev + c2 * (2 / (1 - k^2));

while true
    k = k + 2;
    
    c_k = (2 - (k==0)) / pi * integral(@(x) f(x) .* Tk(k, x) ./ sqrt(1 - x.^2), -1, 1);
    integral_value_new = integral_value + c_k * (2 / (1 - k^2));
    
    % Verificam convergenta
    if abs(integral_value_new - integral_value) < toleranta
        integral_value = integral_value_new;
        break;
    end

    integral_value = integral_value_new;
end

fprintf('Valoarea integralei: %.6f\n', integral_value);
fprintf('Numarul de noduri folosite: %d\n', k);
