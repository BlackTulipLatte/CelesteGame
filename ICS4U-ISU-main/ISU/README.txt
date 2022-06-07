ASSIGNMENT: ISU - CELESTE
DATE: JANUARY 24, 2022
NAME: APRIL WEI & TYLER ZENG
DESCRPTION: A RENDITION OF CELESTE CLASSIC + ORIGINAL CELESTE ON STEAM

RESPONSIBILITIES:
April: Menu screens, map design, collision, animation + hair (drawn on separately), climbing, jumping
Tyler: Menu screens, sound, jumping, leaderboard, pause screen, strawberries, win screen

KNOWN BUGS/ERRORS:
-Changing volume is slightly delayed when the arrow keys are pressed (FloatControl has a minor delay)
-Jumping down and hugging a wall may result in the character not dying when touching the spikes (X and Y coordinates of the character are not updated)
-Might not be able to double jump right after respawning
-When climbing and the opposite direction, the character animation faces the wrong way
-When transitioning levels (eg. level 3-->level 4) the character may clip through the wall OR character may not be placed at the right spot right after transitioning levels
-Character will switch between climbing state and non climbing state when reaching the top of a wall (stamina will deplete only on frames when character is climbing)
-When spawning at level 4, the character flickers

FUNCTIONALITIES REMOVED/CHANGED FROM ORIGINAL PLAN:
-Removed moving terrain
-"Dashed" changed from dash to double jump
-Removed flying strawberries

HINTS ON HOW TO BEAT GAME:
-Try pairing jump + climb + double jump
-The head of the character sticks to the bottom of the block above if the character jumps (Helpful for level 3)

ADDITIONAL INFORMATION:
-When climbing, stamina will consistently deplete (for ~3-5 seconds) before it hits 0. At 0, the character will fall down. Stamina resets when character touches the ground
-Character can only climb if dash is available (to obtain dash, character must touch the ground)
-Character hair will change colour based on dash availability (blue if it is not available)