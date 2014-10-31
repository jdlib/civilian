/*
 * Copyright (C) 2014 Civilian Framework.
 *
 * Licensed under the Civilian License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.civilian-framework.org/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package org.civilian.samples.game;


import java.io.Serializable;
import java.util.Random;


@SuppressWarnings("serial")
public class Game implements Serializable
{
	private static final int INITIAL_GUESSES = 10;
	
	
	public enum Result
	{
		gameover,
		correct,
		invalid,
		wrong
	}
	
	
	public Game()
	{
		this(100);
	}
	
	
	public Game(int max)
	{
		if (max <= 0)
			throw new IllegalArgumentException();
		max_ 	= max;
		random_	= new Random();
		reset();
	}
	
	
	public boolean isNew()
	{
		return remainingGuesses_ == INITIAL_GUESSES;
	}
	

	public int getSmallest()
	{
		return smallest_;
	}
	
	
	public int getBiggest()
	{
		return biggest_;
	}

	
	public int getRemainingGuesses()
	{
		return remainingGuesses_;
	}

	
	/**
	 * Check whether the current guess is correct, and update the biggest/smallest guesses as needed. Give
	 * feedback to the user if they are correct.
	 */
	public Result play(int guess)
	{
		if (remainingGuesses_ <= 0)
			return Result.gameover;
		
		remainingGuesses_--;

		if (guess == number_)
			return Result.correct;
		
		if ((guess < smallest_) || (guess > biggest_))
			return Result.invalid;
		
		if (guess > number_)
			biggest_ = guess - 1;
		else if (guess < number_)
			smallest_ = guess + 1;
		return Result.wrong;
	}


	public void reset()
	{
		smallest_ 			= 0;
		biggest_			= max_;
		remainingGuesses_ 	= INITIAL_GUESSES;
		number_				= random_.nextInt(biggest_ + 1);
	}


//	/**
//	 * A JSF validation method which checks whether the guess is valid. It might not be valid because there
//	 * are no guesses left, or because the guess is not in range.
//	 */
//	public void validateNumberRange(FacesContext context, UIComponent toValidate, Object value)
//	{
//		if (remainingGuesses <= 0)
//		{
//			FacesMessage message = new FacesMessage("No guesses left!");
//			context.addMessage(toValidate.getClientId(context), message);
//			((UIInput)toValidate).setValid(false);
//			return;
//		}
//		int input = (Integer)value;
//
//		if (input < smallest || input > biggest)
//		{
//			((UIInput)toValidate).setValid(false);
//
//			FacesMessage message = new FacesMessage("Invalid guess");
//			context.addMessage(toValidate.getClientId(context), message);
//		}
//	}


    private final int max_;
	private final Random random_;
    private int smallest_;
	private int biggest_;
	private int remainingGuesses_;
	private int number_;
}
