import os
from PIL import Image, ImageDraw

def hex_to_rgb(hex_code):
    hex_code = hex_code.lstrip('#')
    return tuple(int(hex_code[i:i+2], 16) for i in (0, 2, 4)) + (255,)

colors = {
    'O': hex_to_rgb('#303030'), # Outline
    'S': hex_to_rgb('#F0C0A0'), # Skin
    'W': hex_to_rgb('#F0F0F0'), # White (Hat/Shoes)
    'R': hex_to_rgb('#D03030'), # Red (Hat/Jacket)
    'H': hex_to_rgb('#282828'), # Hair
    'B': hex_to_rgb('#E8C830'), # Bag Yellow
    'P': hex_to_rgb('#3870A8'), # Pants Blue
    '.': (0, 0, 0, 0)
}

# 16x20 sprites, will be drawn at offset x=8, y=6
sprites = {
    'player_down.png': [
        "....OOOOOOOO....",
        "...OWWWWWWWWO...",
        "..OWWWWWWWWWWO..",
        "..OWWRRRRRRWWO..",
        "..OHSSSSSSSSHO..",
        "..OHHSOOOOSHOO..",
        "..OOOHSSSSOHOO..",
        "...OORRRRRROO...",
        "..ORRRRRRRRRRO..",
        ".OORRRRRRRRRROO.",
        ".OOBRRRRRRRRBOO.",
        ".OOBRRRRRRRRBOO.",
        ".OOORRRRRRRROOO.",
        "...OPPPPPPPPO...",
        "...OPPPPPPPPO...",
        "...OPPPPPPPPO...",
        "...OWOO..OOWO...",
        "..OWWWO..OWWWO..",
        "..OOOOO..OOOOO..",
        "................"
    ],
    'player_up.png': [
        "....OOOOOOOO....",
        "...ORRRRRRRRO...",
        "..ORRRRRRRRRRO..",
        "..ORRRRRRRRRRO..",
        "..OHHHHHHHHHHO..",
        "..OHHHHHHHHHHO..",
        "..OOOHHHHHOHOO..",
        "...OORRRRRROO...",
        "..ORRRRRRRRRRO..",
        ".OORBBBRRRRRROO.",
        ".OORBBBRRRRRROO.",
        ".OORBBBRRRRRROO.",
        ".OOORRRRRRRROOO.",
        "...OPPPPPPPPO...",
        "...OPPPPPPPPO...",
        "...OPPPPPPPPO...",
        "...OWOO..OOWO...",
        "..OWWWO..OWWWO..",
        "..OOOOO..OOOOO..",
        "................"
    ],
    'player_left.png': [
        "......OOOOOO....",
        ".....OWWWWWWO...",
        "....OWWWWWWWWO..",
        "...ORRRWWWWWWO..",
        "...OSSSSSSSSHO..",
        "...OSSOOOSSHOO..",
        "....OSSSSSHOO...",
        ".....ORRRROO....",
        "....ORRRRRROO...",
        "...ORRBBBRRRO...",
        "..OORRBBBRRROO..",
        "..OORRBBBRRROO..",
        "...OORRRRRROO...",
        "....OPPPPPPO....",
        "....OPPPPPPO....",
        "....OPPPPPPO....",
        "....OWOOOOWO....",
        "...OWWWOOWWWO...",
        "...OOOOOOOOOO...",
        "................"
    ],
    'player_right.png': [
        "....OOOOOO......",
        "...OWWWWWWO.....",
        "..OWWWWWWWWO....",
        "..OWWWWWWRRRO...",
        "..OHSSSSSSSSO...",
        "..OOHSSOOOSSO...",
        "...OOHSSSSSO....",
        "....OORRRRO.....",
        "...OORRRRRRO....",
        "...ORRRBBBRRO...",
        "..OORRRBBBRROO..",
        "..OORRRBBBRROO..",
        "...OORRRRRROO...",
        "....OPPPPPPO....",
        "....OPPPPPPO....",
        "....OPPPPPPO....",
        "....OWOOOOWO....",
        "...OWWWOOWWWO...",
        "...OOOOOOOOOO...",
        "................"
    ]
}

out_dir = '/Users/henrique/pokedungeon/assets/sprites/player'
os.makedirs(out_dir, exist_ok=True)

for name, rows in sprites.items():
    img = Image.new('RGBA', (32, 32), (0, 0, 0, 0))
    pixels = img.load()
    
    start_x = 8
    start_y = 6
    
    for y, row_str in enumerate(rows):
        for x, char in enumerate(row_str):
            if char in colors:
                pixels[start_x + x, start_y + y] = colors[char]
                
    img.save(os.path.join(out_dir, name))

print("Player sprites generated successfully!")
