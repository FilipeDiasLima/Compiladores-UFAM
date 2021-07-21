' Exemplos de loops em uBASIC 
Global i
Function loop(n)
	Dim j
	'While
	Print "While: [ "
	i = 1
	While i <= n
		Print i, " "
		i = i + 1
	End While
	Print "]\n"
	'For
	Print "For: [ "
	For j = 1 To n
		Print j, " "
	Next j
	Print "]\n"
	Return j
End Function

Print loop(10)
