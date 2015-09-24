% build0010.t
import GUI
View.Set ("noecho,nocursor,nobuttonbar,graphics:600;400")

% Declare Variables
var button : int := 0
var delayTime : int := 10
var level : int := 0
var collision : boolean := false

var hero : int := Pic.FileNew ("hero.jpg")
var billGates : int := Pic.FileNew ("evilgates.jpg")
var billSpeed : int := 0

var heroX, heroY : int := maxy div 2
const HERO_SPEED := 8

var billX : int := 0
var billY : int := 0

var delayCall : int := 3000

var font : int := Font.New ("serif:12")
var largeText : int := Font.New ("verdana:100")
var intCounter : int

proc blueRec
    drawfillbox (0, maxy, maxx, maxy - 20, blue)
end blueRec

proc map
    drawfillbox (0, maxy - 20, maxx, 0, darkgrey)
end map

proc theHero
    Pic.Draw (hero, heroX - 12, heroY - 12, picMerge)
end theHero

proc BillGates
    Pic.Draw (billGates, billX - 40, billY - 40, picMerge)
end BillGates

process countdown
    for decreasing counter : 30 .. 1

	var stringCounter : string

	stringCounter := intstr (counter)
	intCounter := strint (stringCounter)


	blueRec

	Draw.Text ("Time Left:", 230, maxy - 15, font, white)
	Draw.Text (stringCounter, 300, maxy - 15, font, white)
	Draw.Text ("seconds.", 325, maxy - 15, font, white)

	delay (1000)
	Draw.Cls

	exit when collision = true

    end for
end countdown

fcn checkCollision : boolean
    if Math.Distance (heroX, heroY, billX, billY) <= 40 then
	result true
    end if

    result false
end checkCollision

proc checkPos
    if heroX < 12 then
	heroX := 12
    elsif heroX > maxx - 13 then
	heroX := maxx - 13
    end if

    if heroY < 12 then
	heroY := 12
    elsif heroY > maxy - 33 then
	heroY := maxy - 33
    end if

    if billY > maxy - 30 then
	billY := maxy - 20
    end if
end checkPos

proc move
    var x, y : int

    Mouse.Where (x, y, button)

    if x not= heroX or y not= heroY then

	var d : real := sqrt ((x - heroX) ** 2 + (y - heroY) ** 2)
	var dx : int := round ((x - heroX) * HERO_SPEED / d)
	var dy : int := round ((y - heroY) * HERO_SPEED / d)

	if abs (dx) > abs (x - heroX) then
	    heroX := x
	else
	    heroX := heroX + dx
	end if

	if abs (dy) > abs (y - heroY) then
	    heroY := y
	else
	    heroY := heroY + dy
	end if

	checkPos

	theHero
    end if

    View.Update
end move

proc moveGates
    if heroX not= billX or heroY not= billY then

	var d : real := sqrt ((heroX - billX) ** 2 + (heroY - billY) ** 2)
	var dx : int := round ((heroX - billX) * billSpeed / d)
	var dy : int := round ((heroY - billY) * billSpeed / d)

	if abs (dx) > abs (heroX - billX) then
	    billX := heroX
	else
	    billX := billX + dx
	end if

	if abs (dy) > abs (heroY - billY) then
	    billY := heroY
	else
	    billY := billY + dy
	end if

	checkPos
	BillGates
    end if

    View.Update
end moveGates

% This is the main program loop
loop
    for fiveSeconds : 1 .. 500
	map
	theHero
	move
	blueRec

	Draw.Text ("You have 5 seconds to prepare before Bill attacks!", maxx div 4, maxy - 15, font, white)
    end for

    blueRec

    billSpeed += 1
    level += 1

    loop
	fork countdown

	billX := Rand.Int (0, maxx)
	billY := Rand.Int (0, maxy - 20)

	loop
	    map
	    theHero
	    move
	    BillGates
	    moveGates
	    delay (delayTime)

	    collision := checkCollision

	    exit when collision = true

	    if intCounter = 1 then
		exit
	    end if

	end loop


	exit when collision = true
	if intCounter = 1 then
	    exit
	end if

    end loop

    exit when collision = true
    exit when level = 6

end loop

Draw.Cls

if level = 6 then
    put "Congratulation, you have beaten the game."
    delay (1000)
elsif collision = true then
    put "HAHA! Bill has caught you and eaten you. Fail."
    delay (1000)
end if

loop
    exit when GUI.ProcessEvent
end loop
