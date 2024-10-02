function myrealmax()

    maxExp = 2 - myeps();  % initializam cu o putere a lui 2
    while (~isinf(maxExp))  % cat timp numarul nu ajunge la overflow
        bv = maxExp;    % retinem exponentul cel mai mare
        maxExp = 2*maxExp;  % marim exponentul
    end
    fprintf("My real maxim: %d \n", h);
    fprintf("System real maxim: %d \n", realmax);

end