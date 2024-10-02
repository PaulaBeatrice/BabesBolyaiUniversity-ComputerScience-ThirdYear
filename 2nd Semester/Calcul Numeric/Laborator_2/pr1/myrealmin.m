function myrealmin()
    power = 2^(-52);  % initializam cu o putere mica
    bv = 0;
    while power > 0
        bv = power;
        power = power / 2;
    end

    res = bv / eps;

    fprintf("My real minim: %d \n", res);
    fprintf("System real minim: %d \n", realmin);
    % realmin = 2^(-1022)
end