unit
module windows
    export commands, input, enter

    procedure inventory
	var winID : int := 0
	winID := Window.Open ("position:top;middle,graphics:300;150")
	put "YAUS!"
    end inventory

    procedure commands (input : string)
	if input = "i"
		then
	    inventory
	else
	    put "not duane!"
	end if
    end commands

    procedure input
	var dialog : int := 0
	var input : string
	dialog := Window.Open ("position:top;center,graphics:300;150")
	get input
	commands (input)
    end input

    process enter
	var chars : array char of boolean
	var down : boolean
	loop
	    Input.KeyDown (chars)
	    if chars (KEY_ENTER) and down = false then
		input
		down := true
	    else
		down := false
	    end if
	end loop
    end enter
end windows
