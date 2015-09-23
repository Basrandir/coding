#include <iostream>
#include <string>
#include <sstream>
using namespace std;

string binaryRepresentation(int num, string binary)
{
	stringstream temp;
	temp << (num % 2);

	if (num == 0)
		return binary;
	else
		return binaryRepresentation(num / 2, binary += temp.str());
}

int main()
{
	int number;
	string binary;

	cout << "Enter a number that we will change to binary: ";
	cin >> number;
	string binaryNum = binaryRepresentation(number, "");
	
	for (int i = 0 ; i < binaryNum.length() ; i++)
		binary += binaryNum[(binaryNum.length() - 1) - i];

	cout << binary;
	return 0;
}
