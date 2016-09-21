import matplotlib.pyplot as plt
import numpy as np
import math
import random
from copy import deepcopy

class SparseVector:
	#uses nested dictionaries
	def __init__(self):
		self.my_data = {}
	def setMe(self, movie, user, rating):
		if movie in self.my_data:
			# my_score = {}
			# my_score[user] = rating	#add new pair to inner dictionary
			self.my_data[movie][user] = rating
			return
		my_row = {}
		my_row[user] = rating		#add new dictionary and append
		self.my_data[movie]= my_row
		return

	def getAverage(self):
		total = 0
		count = 0
		for movie in self.my_data:
			for user in movie:
				total += movie[user]
				count += 1
		return total/count

	def getMean(self, movie1, movie2):
		m1 = self.my_data[movie1]
		m2 = self.my_data[movie2]
		mean_movie = {}
		for person in m1:
			if person in m2:
				mean_rating = (m1[person] + m2[person])/2
			else:
				mean_rating = m1[person]
			mean_movie[person] = mean_rating
		for person in m2:
			if not person in mean_movie:
				mean_movie[person] = m2[person]
		return mean_movie

def getClosest(point, centroids):
	min_dist = float('inf')
	for centroid in centroids.my_data:
		dist = getDistance(point, centroids.my_data[centroid])
		if dist < min_dist:
			min_dist = dist
			closest = centroid
	return closest

def getDistance(m1, m2):
	dist = 0
	count = 0
	connect = 0
	# print(m1)
	for person in m1:
		if person in m2:
			dist += abs(m1[person]-m2[person])
			connect = 1
	if connect:
		distance = dist/count
	else:
		distance = 1000
	return distance;

def cluster(inp, k):
	centroids = SparseVector()
	points = inp[0]
	titles = inp[1]
	for i in range(k):
		movie = random.choice(list(points.my_data.keys()))
		human = random.choice(list(points.my_data[movie].keys()))
		rating = points.my_data[movie][human]
		centroids.setMe(movie, human, rating)
	domains = {}
	epoch = 0
	while 1 == 1:
		oldDomains = deepcopy(domains)
		for point in points.my_data:
			my_centroid = getClosest(points.my_data[point], centroids)
			if my_centroid in domains:
				for human in points.my_data[point]:
					rating = points.my_data[point][human]
					domains[my_centroid].setMe(point,human,rating)
			else:
				domain = SparseVector()
				for human in points.my_data[point]:
					rating = points.my_data[point][human]
					domain.setMe(point, human, rating)
				domains[my_centroid] = domain
		epoch += 1
		print epoch
		if (oldDomains == domains) or epoch > 32:
			for domain in domains:
				print "domain"
				for movie in domains[domain].my_data:
					print movie
					print titles[np.int_(movie) - 1]
			break

def loadDataset(filename='u.data', filename2 = 'u.item'):
	in_data = np.genfromtxt(filename)
	titles = open(filename2).readlines()
	movies = SparseVector()
	for row in in_data:
		movies.setMe(row[0],row[1],row[2])
	return (movies, titles)

if __name__=="__main__":
	movies = loadDataset()
	x = cluster(movies, 10)
