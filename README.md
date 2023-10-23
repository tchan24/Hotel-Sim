# Hotel-Sim
A hotel is simulated by using threads and semaphores to model customer and employee behavior.  

## How to run  
'''shell
javac HotelSimulation.java
java HotelSimulation.java
'''  

## Description  
25 Guests, 2 Front Desk Employees, 2 Bellhops  

### Sample Output  
'''shell
Simulation starts
Front desk employee 0 created
Front desk employee 1 created
Bellhop 0 created
Bellhop 1 created
Guest 0 created
Guest 1 created
Guest 2 created
Guest 0 enters hotel with 1 bag
Guest 1 enters hotel with 4 bags
Guest 2 enters hotel with 3 bags
Front desk employee 0 registers guest 0 and assigns room 1
Front desk employee 1 registers guest 1 and assigns room 2
Guest 0 receives room key for room 1 from front desk employee 0
Guest 1 receives room key for room 2 from front desk employee 1
Front desk employee 0 registers guest 2 and assigns room 3
Guest 0 enters room 1
Guest 2 receives room key for room 3 from front desk employee 0
Guest 1 requests help with bags
Guest 0 retires for the evening
Guest 0 joined
Guest 2 requests help with bags
Bellhop 1 receives bags from guest 2
Bellhop 0 receives bags from guest 1
Guest 1 enters room 2
Guest 2 enters room 3
Bellhop 0 delivers bags to guest 1
Bellhop 1 delivers bags to guest 2
Guest 1 receives bags from bellhop 0 and gives tip
Guest 2 receives bags from bellhop 1 and gives tip
Guest 2 retires for the evening
Guest 1 retires for the evening
Guest 1 joined
Guest 2 joined
Simulation ends
'''