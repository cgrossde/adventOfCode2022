package main

import (
	"bufio"
	"fmt"
	"log"
	"os"
	"strconv"
)

func Day01() int {
	file, err := os.Open("src/Day01.txt")
	if err != nil {
		log.Fatal(err)
	}
	defer file.Close()
	scanner := bufio.NewScanner(file)

	maxCalories := 0
	currentElfCalories := 0

	for scanner.Scan() {
		line := scanner.Text()
		if line == "" {
			currentElfCalories = 0
		}

		calories, _ := strconv.Atoi(line)
		currentElfCalories += calories
		if currentElfCalories > maxCalories {
			maxCalories = currentElfCalories
		}
	}

	if err := scanner.Err(); err != nil {
		log.Fatal(err)
	}

	return maxCalories
}

func main() {
	fmt.Println("Max calories:", Day01())
}
