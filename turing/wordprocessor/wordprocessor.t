% Program Name: n00b "Lacking Mac" Word Processor
% Names: Bassam Savage
% Date: September 30th, 2006
% Purpose: In order to create a word processor

import GUI

/***********************
 Declaring the Variables
 ***********************/

var boxID, clrtext, quitbutton, start : int
var text : string

%Variable for the Menus
var menu1, menu2 : int
var menuitems : array 1 .. 7 of int
var name : array 1 .. 7 of string :=
    init ("New", "Open", "Save", "Quit", "Help on Lacking Mac", "Reference", "About")

% This is the core of the whole program. It's a very simple variable and input system. You input something, then it loops back to another input
process window
    setscreen ("graphics")
    locatexy (0, 365)
    loop
	get text
    end loop
end window
fork window

% Closes the program.
procedure Quit
    quit
end Quit

% This deletes everything on the screen, and puts the cursor back at the top. For some reason, this deletes the button, too, so the button gets remade.
procedure ClearText
    GUI.ClearText (boxID)
    cls
    % Pretty much simply remakes the buttons below after they get deleted
    clrtext := GUI.CreateButton (0, 0, 0, "Clear Text", ClearText)
    menu1 := GUI.CreateMenu ("File")
    for cnt : 1 .. 4
	menuitems (cnt) := GUI.CreateMenuItem (name (cnt), Quit)
    end for
    menu1 := GUI.CreateMenu ("Help")
    for cnt : 5 .. 7
	menuitems (cnt) := GUI.CreateMenuItem (name (cnt), Quit)
    end for
    boxID := GUI.CreateTextBox (0, 25, 675, 350)
end ClearText

% Creates the widgets on the screen
boxID := GUI.CreateTextBox (0, 25, 675, 350)
clrtext := GUI.CreateButton (0, 0, 0, "Clear Text", ClearText)

/************
 Menus
 ************/

%Creating Menu(s)
menu1 := GUI.CreateMenu ("File")
for cnt : 1 .. 4
    menuitems (cnt) := GUI.CreateMenuItem (name (cnt), Quit)
end for

menu1 := GUI.CreateMenu ("Help")
for cnt : 5 .. 7
    menuitems (cnt) := GUI.CreateMenuItem (name (cnt), Quit)
end for

loop
    exit when GUI.ProcessEvent
end loop
