%include 'io.inc'
section .data 
	 b times 4 db 0
	 a times 4 db 0
global main 
 section .text 
 main:
		mov eax, a
		call io_readint
		mov [a] , eax
		mov AL, 1
		add AL, [a]
		mov AH, 0
		mov DL, 2
		div DL
		mov [b], AL

		mov eax, [b]
		call io_writeint

