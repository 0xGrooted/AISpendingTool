# ML For Spending
An Machine Learning tool, that takes into account the Frequency, Magnitude &amp; Discretion then will Analyse through the transactions and identify potential ways to save. It develops a SPS ( Savings Priority Score ) And then ranks them and outputs to the user. I used my own data :D but please have a look at the following for more information.
We use Weighted Summation Machine learning:

<img width="624" height="332" alt="image" src="https://github.com/user-attachments/assets/88bcace6-21cf-4ec9-b0b7-dafdaba3a73f" />

This is a simpler approach and uses simple maths to calculate the output. 

# POC / Evidence it works :)

<img width="837" height="636" alt="Screenshot 2025-10-17 at 19 16 44" src="https://github.com/user-attachments/assets/2bb5bfb1-5377-46b4-9b30-c6778404b8f5" />


<img width="1190" height="257" alt="Screenshot 2025-10-17 at 19 17 09" src="https://github.com/user-attachments/assets/b649da3e-1a8e-4f4a-b0c5-1ba1947f1ec0" />

Simply read through the code and my comments from here and in the java file to understand the code.



# 1.1 Class - SpendingCategory
An enumeration class , These will represent the flexibility of the different types of spending the user does.
Pretty simple to follow: Spending money on a house will be less flexibile than spending money on luxury goods.

I picked an Enumeration class because all of the values are fixed in the class and do not need to change. 

# 1.2 Class - Transaction
A class that will be used for the transactions, This will be referenced further on into the program.

# 1.3 Class - SavingsOpportunity
This is a class used to store the savings opportunites the user may have. 

------------------------

# 2.1 Class - WeightedScoringAnalyzer
This class is where the 'magic' happens (If you believe this I am terribly sorry, my logic in here feels like it may be flawed and would really appreciate a review here.
I have not got much experience with ML and AI at all, so any advice or issues please fire away :)

Okay so to begin I am using this as a way to teach myself and fill in any gaps so I will include links to anywhere I used to code and teach myself.
We have four 4 variables, we determine how 'important' or how much we want the machine to take into account this variable.
Find out more here: https://www.geeksforgeeks.org/deep-learning/the-role-of-weights-and-bias-in-neural-networks/

1. WEIGHT_FREQUENCY = 0.3 : This is how many times the purchase type comes up
2. WIGHT_MAGNITUDE = 0.4 : This is essentially how important we deem the total amount that is spent to be.
3. WEIGHT_DISCRETION = 0.5 : This is how important the flexibility is to the 
4. BASELINE_ADJUSTMENT = 0.1 : Apparently this is not neccessary but it gives each of the neurons in the neural network a small start bonus.

# 2.2 HashMap
So this is now used alongside 1.1 we want the machine to be able to pair the spendingCategory with a value.
The higher the value the more flexibile (I spent money on say A new jLuxury jumper but was this neccessary / flexible )?

So this explains the reasoning behind giving these valuesI hope :)

I had a bit of difficuly remembering how to call the hashmap so I used the following: https://www.freecodecamp.org/news/how-java-hashmaps-work-internal-mechanics-explained/

# 2.3 List - runAnalysis
Here we begin our scoring system I decided to output a LOT out to the user I think it makes it a lot easier to follow....
I would be lying if I didn't use ChatGPT here I had no idea how to go through the expenses and feed it to the netowrk ;)

So the first will calculate the frequency of how how many transactions happen in each category.
This is done using a map / lookup table.

The second will calculate how much you spend in each of the categories.

Now we get the maximum values from these and compare these!

# 2.4 - ArrayList Opportunities
My favourite :)
I love arraylists they are my lionel messi in coding. 
Essentially what we do in here is identify the best opportunities for the user. 

Hey you made it this far ! Thank you :) 
This is more for me and to va;idate my learning but thank you.

-----------------

I plan to do line by line code review where applicable so keep an eye out :)








