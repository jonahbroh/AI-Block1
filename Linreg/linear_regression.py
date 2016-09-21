import matplotlib.pyplot as plt
import numpy as np
np.random.seed(42) # Get the same random numbers every time


# Ugly code for thinking about linear regression with gradient descent

################################################################
### Load the dataset
my_data = np.genfromtxt('djia_temp.csv', delimiter=';', skip_header=1)[:21]
djiac = my_data[:, 1]/1000
print djiac
high_temp = my_data[:, 2] - my_data[:, 3]
print high_temp



################################################################
### Init the model parameters
weight = np.random.rand(1)
bias = np.random.rand(1)
print weight, bias


################################################################
### How do we change the weight and the bias to make the line's fit better?
learning_rate = 0.05
final_rate = .001



for i in range(1000):
    # for i in range(len(djiac)):
    if learning_rate > final_rate:
        learning_rate *= .995
    error = (high_temp*weight+bias) - djiac
    print error
    weight = weight - np.sum(learning_rate * error * high_temp / len(high_temp))
    print weight, "a"
    bias = bias - np.sum(learning_rate * error * 1.0 / len(high_temp))



# print (end_end_cost)

################################################################
### Graph the dataset along with the line defined by the model
#
xs = np.arange(-20, 20)
print xs
ys = xs * weight + bias

plt.plot(high_temp, djiac, 'r+', xs, ys, 'g-')
plt.show()
