% Program Name: n00b "Lacking Mac" Word Processor
% Names: Bassam Savage
% Date: September 30th, 2006
% Purpose: In order to create a word processor

import GUI
View.Set ("text:1024;768,nobuttonbar")
var boxID, clrtext, quitbutton, start : int
var text : string
var aboutwindow : int

% Menu variables
var menu1, menu2 : int
var menuitems : array 1 .. 7 of int
var name : array 1 .. 7 of string :=
    init ("New", "Load", "Save", "Quit", "Yaus", "Checkerbox", "About")
var visible : boolean := false

/******************
 Menu Procedures
 ******************/

% Closes the program.
procedure Quit
    quit
end Quit

% Does nothing because some of our buttons don't have a job yet
procedure DoNothing
end DoNothing

% Closes the About Window
procedure AboutClose
    Window.Hide (aboutwindow)
end AboutClose

% Opens the About Window
procedure About
    aboutwindow := Window.Open ("position:top;left,graphics:500;500")
    put "This program was designed and developed by Bassam Saeed and   Nick Savage, otherwise known as Bassam Savage."
    put ""
    put "YAUS CHECKERBOX!"
    var closewindow := GUI.CreateButton (maxx div 3, 0, 0, "Close Window", AboutClose)
end About

/******************
 Widgets and Typing
 ******************/

% This is the core of the whole program. It's a very simple variable and input system. You input something, then it loops back to another input
process window
    setscreen ("graphics")
    locatexy (0, 365)
    loop     % Problem with the loop is that one cannot delete what one has written on the previous line, that's where the ClearText comes in
	get text
    end loop
end window
fork window

% This deletes all the text on the screen. For some reason, this duplicates the menu.
procedure ClearText
    GUI.ClearText (boxID)
    cls
    Text.Locate (3, 1)
    GUI.Refresh     % Technically counteracts the issues dealt by cls
    menu1 := GUI.CreateMenu ("File")
    menu1 := GUI.CreateMenu ("Oh Yeah, And")
end ClearText

% Creates the widgets on the screen
boxID := GUI.CreateTextBox (800, 0, 0, 0)
clrtext := GUI.CreateButton (0, 0, 0, "ClearText", ClearText)

/******************
 Displaying Menus
 ******************/

% Creating Menu(s)
menu1 := GUI.CreateMenu ("File")
for cnt : 1 .. 3
    menuitems (cnt) := GUI.CreateMenuItem (name (cnt), DoNothing)
end for
menuitems (4) := GUI.CreateMenuItem (name (4), Quit)

menu1 := GUI.CreateMenu ("Oh Yeah, And")
for cnt : 5 .. 6
    menuitems (cnt) := GUI.CreateMenuItem (name (cnt), DoNothing)
end for
menuitems (7) := GUI.CreateMenuItem (name (7), About)

loop
    exit when GUI.ProcessEvent
end loop
