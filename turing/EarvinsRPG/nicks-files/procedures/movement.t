View.Set ("graphics:640;480")

drawfillbox (0, maxy, maxx, 0, green)
for i : 0 .. 640 by 20
    drawfillbox (i, 20, i + 20, 0, 4)
end for

procedure drawChar (position : array 1 .. 2 of int)
    drawfillbox (position (1), position (2), position (1) + 20, position (2) - 20, 5)
end drawChar

function moveChar : array 1 .. 2 of int
    % Variables
    var chars : array char of boolean
    var charPlace : array 1 .. 2 of int
    var x : int := 50
    var y : int := 50

    charPlace (1) := x
    charPlace (2) := y

    loop
	Input.KeyDown (chars)
	if chars (KEY_UP_ARROW) then
	    charPlace (2) := (y + 1)
	    drawChar (charPlace)
	elsif chars (KEY_DOWN_ARROW) then
	    charPlace (2) := (y - 1)
	    drawChar (charPlace)
	elsif chars (KEY_LEFT_ARROW) then
	    charPlace (1) := (x - 1)
	    drawChar (charPlace)
	elsif chars (KEY_RIGHT_ARROW) then
	    charPlace (1) := (x + 1)
	    drawChar (charPlace)
	end if
    end loop
    result charPlace
end moveChar

process getKey
    var yaus : array 1 .. 2 of int
    loop
	yaus := moveChar
    end loop
end getKey


fork getKey


