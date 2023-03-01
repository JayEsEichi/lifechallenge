package com.example.lifechallenge.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public enum Difficulty {

    easy("쉬움", 1, "⭐"),
    normal("보통", 2, "⭐⭐"),
    hard("어려움", 3, "⭐⭐⭐");

    private String difficulty;
    private int difficultyLevel;
    private String starRate;


    public static Difficulty inputDifficulty(int difficulty){
        for(Difficulty eachDifficulty : Difficulty.values()){
            if(eachDifficulty.difficultyLevel == difficulty){
                return eachDifficulty;
            }
        }
        return null;
    }

}
