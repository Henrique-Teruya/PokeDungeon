import os
from PIL import Image, ImageDraw

def hex_to_rgb(hex_code):
    hex_code = hex_code.lstrip('#')
    return tuple(int(hex_code[i:i+2], 16) for i in (0, 2, 4)) + (255,)

C_FLOOR_L = hex_to_rgb('#C8B888')
C_WATER = hex_to_rgb('#5890C8')
C_WATER_D = hex_to_rgb('#3870A8')
C_OUTLINE = hex_to_rgb('#303030')

out_dir = '/Users/henrique/pokedungeon/assets/tiles'
os.makedirs(out_dir, exist_ok=True)

def draw_ripples(draw, random_seed):
    import random
    random.seed(random_seed)
    for _ in range(4):
        x = random.randint(4, 24)
        y = random.randint(4, 24)
        draw.line([x, y, x+4, y], fill=C_WATER_D, width=1)
        draw.line([x+2, y+1, x+6, y+1], fill=C_WATER_D, width=1)

# Base water (Center)
img = Image.new('RGBA', (32, 32), C_WATER)
draw = ImageDraw.Draw(img)
draw_ripples(draw, 42)
img.save(os.path.join(out_dir, 'water_center.png'))

# Edges
# Top
img = Image.new('RGBA', (32, 32), C_FLOOR_L)
draw = ImageDraw.Draw(img)
draw.rectangle([0, 4, 31, 31], fill=C_WATER)
draw.line([0, 4, 31, 4], fill=C_OUTLINE, width=1)
draw_ripples(draw, 43)
img.save(os.path.join(out_dir, 'water_n.png'))

# Bottom
img = Image.new('RGBA', (32, 32), C_FLOOR_L)
draw = ImageDraw.Draw(img)
draw.rectangle([0, 0, 31, 27], fill=C_WATER)
draw.line([0, 27, 31, 27], fill=C_OUTLINE, width=1)
draw_ripples(draw, 44)
img.save(os.path.join(out_dir, 'water_s.png'))

# Left
img = Image.new('RGBA', (32, 32), C_FLOOR_L)
draw = ImageDraw.Draw(img)
draw.rectangle([4, 0, 31, 31], fill=C_WATER)
draw.line([4, 0, 4, 31], fill=C_OUTLINE, width=1)
draw_ripples(draw, 45)
img.save(os.path.join(out_dir, 'water_w.png'))

# Right
img = Image.new('RGBA', (32, 32), C_FLOOR_L)
draw = ImageDraw.Draw(img)
draw.rectangle([0, 0, 27, 31], fill=C_WATER)
draw.line([27, 0, 27, 31], fill=C_OUTLINE, width=1)
draw_ripples(draw, 46)
img.save(os.path.join(out_dir, 'water_e.png'))

# Corners
# Top-Left
img = Image.new('RGBA', (32, 32), C_FLOOR_L)
draw = ImageDraw.Draw(img)
draw.rectangle([4, 4, 31, 31], fill=C_WATER)
draw.line([4, 4, 31, 4], fill=C_OUTLINE, width=1)
draw.line([4, 4, 4, 31], fill=C_OUTLINE, width=1)
draw_ripples(draw, 47)
img.save(os.path.join(out_dir, 'water_nw.png'))

# Top-Right
img = Image.new('RGBA', (32, 32), C_FLOOR_L)
draw = ImageDraw.Draw(img)
draw.rectangle([0, 4, 27, 31], fill=C_WATER)
draw.line([0, 4, 27, 4], fill=C_OUTLINE, width=1)
draw.line([27, 4, 27, 31], fill=C_OUTLINE, width=1)
draw_ripples(draw, 48)
img.save(os.path.join(out_dir, 'water_ne.png'))

# Bottom-Left
img = Image.new('RGBA', (32, 32), C_FLOOR_L)
draw = ImageDraw.Draw(img)
draw.rectangle([4, 0, 31, 27], fill=C_WATER)
draw.line([4, 0, 4, 27], fill=C_OUTLINE, width=1)
draw.line([4, 27, 31, 27], fill=C_OUTLINE, width=1)
draw_ripples(draw, 49)
img.save(os.path.join(out_dir, 'water_sw.png'))

# Bottom-Right
img = Image.new('RGBA', (32, 32), C_FLOOR_L)
draw = ImageDraw.Draw(img)
draw.rectangle([0, 0, 27, 27], fill=C_WATER)
draw.line([27, 0, 27, 27], fill=C_OUTLINE, width=1)
draw.line([0, 27, 27, 27], fill=C_OUTLINE, width=1)
draw_ripples(draw, 50)
img.save(os.path.join(out_dir, 'water_se.png'))

print("Lake tiles generated successfully!")
