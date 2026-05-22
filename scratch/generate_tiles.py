import os
from PIL import Image, ImageDraw

def hex_to_rgb(hex_code):
    hex_code = hex_code.lstrip('#')
    return tuple(int(hex_code[i:i+2], 16) for i in (0, 2, 4)) + (255,)

# Palette
C_FLOOR_L = hex_to_rgb('#C8B888')
C_FLOOR_D = hex_to_rgb('#A89868')
C_WALL = hex_to_rgb('#686868')
C_WALL_H = hex_to_rgb('#989898')
C_GRASS = hex_to_rgb('#68A848')
C_GRASS_D = hex_to_rgb('#488830')
C_WATER = hex_to_rgb('#5890C8')
C_WATER_D = hex_to_rgb('#3870A8')
C_OUTLINE = hex_to_rgb('#303030')
C_GOLD = hex_to_rgb('#E8C830')
C_WOOD = hex_to_rgb('#8b5a2b') # custom wood
C_WOOD_D = hex_to_rgb('#5c3a21')
C_BLACK = (0, 0, 0, 255)
C_TRANS = (0, 0, 0, 0)

out_dir = '/Users/henrique/pokedungeon/assets/tiles'
os.makedirs(out_dir, exist_ok=True)

# 1. Floor
img = Image.new('RGBA', (32, 32), C_FLOOR_L)
draw = ImageDraw.Draw(img)
for y in range(0, 32, 8):
    for x in range(0, 32, 8):
        if (x + y) % 16 == 0:
            draw.rectangle([x, y, x+7, y+7], fill=C_FLOOR_D)
img.save(os.path.join(out_dir, 'floor.png'))

# 2. Wall
img = Image.new('RGBA', (32, 32), C_WALL)
draw = ImageDraw.Draw(img)
# Draw bricks
for y in range(0, 32, 8):
    offset = 8 if (y // 8) % 2 == 1 else 0
    for x in range(-8, 32, 16):
        draw.rectangle([x + offset, y, x + offset + 15, y + 7], outline=C_OUTLINE, width=1)
        draw.line([x + offset + 1, y + 1, x + offset + 14, y + 1], fill=C_WALL_H) # highlight
img.save(os.path.join(out_dir, 'wall.png'))

# 3. Door
img = Image.new('RGBA', (32, 32), C_FLOOR_D) # Floor as background for the doorway
draw = ImageDraw.Draw(img)
# Draw arched opening
draw.rectangle([8, 12, 23, 31], fill=C_BLACK)
draw.ellipse([8, 4, 23, 19], fill=C_BLACK)
# Outline for the door
draw.ellipse([7, 3, 24, 20], outline=C_OUTLINE, width=1)
draw.line([7, 12, 7, 31], fill=C_OUTLINE)
draw.line([24, 12, 24, 31], fill=C_OUTLINE)
img.save(os.path.join(out_dir, 'door.png'))

# 4. Chest
img = Image.new('RGBA', (32, 32), C_FLOOR_L) # Floor background
draw = ImageDraw.Draw(img)
# Chest base
draw.rectangle([4, 12, 27, 26], fill=C_WOOD, outline=C_OUTLINE, width=1)
# Chest lid
draw.rectangle([4, 10, 27, 16], fill=C_WOOD_D, outline=C_OUTLINE, width=1)
# Metal bands
draw.rectangle([8, 10, 10, 26], fill=C_WALL_H, outline=C_OUTLINE)
draw.rectangle([21, 10, 23, 26], fill=C_WALL_H, outline=C_OUTLINE)
# Lock
draw.rectangle([14, 14, 17, 18], fill=C_GOLD, outline=C_OUTLINE)
img.save(os.path.join(out_dir, 'chest.png'))

# 5. Grass
img = Image.new('RGBA', (32, 32), C_GRASS)
draw = ImageDraw.Draw(img)
import random
random.seed(42)
for _ in range(15):
    x = random.randint(2, 28)
    y = random.randint(4, 28)
    # blade of grass
    draw.line([x, y, x-2, y-3], fill=C_GRASS_D, width=1)
    draw.line([x, y, x, y-4], fill=C_GRASS_D, width=1)
    draw.line([x, y, x+2, y-2], fill=C_GRASS_D, width=1)
img.save(os.path.join(out_dir, 'grass.png'))

# 6. Water
img = Image.new('RGBA', (32, 32), C_WATER)
draw = ImageDraw.Draw(img)
random.seed(42)
for _ in range(8):
    x = random.randint(2, 24)
    y = random.randint(2, 28)
    draw.line([x, y, x+4, y], fill=C_WATER_D, width=1)
    draw.line([x+2, y+1, x+6, y+1], fill=C_WATER_D, width=1)
img.save(os.path.join(out_dir, 'water.png'))

print("Tiles generated successfully!")
