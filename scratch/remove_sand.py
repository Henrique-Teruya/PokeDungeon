import math
from PIL import Image

def recreate_corner(img_path, corner):
    img = Image.new("RGBA", (32, 32))
    pixels = img.load()
    w, h = 32, 32
    
    c_transparent = (0, 0, 0, 0)
    c_border = (48, 48, 48, 255)
    c_water = (88, 144, 200, 255)
    
    for y in range(h):
        for x in range(w):
            if corner == "nw":
                cx, cy = 31.5, 31.5
            elif corner == "ne":
                cx, cy = -0.5, 31.5
            elif corner == "sw":
                cx, cy = 31.5, -0.5
            elif corner == "se":
                cx, cy = -0.5, -0.5
                
            dist = math.hypot(cx - x, cy - y)
            
            if dist >= 27.8:
                pixels[x, y] = c_transparent
            elif dist >= 26.5:
                pixels[x, y] = c_border
            else:
                pixels[x, y] = c_water
                
    img.save(img_path)

def remove_sand(img_path):
    img = Image.open(img_path).convert("RGBA")
    pixels = img.load()
    w, h = img.size
    c_sand = (200, 184, 136, 255)
    c_transparent = (0, 0, 0, 0)
    for y in range(h):
        for x in range(w):
            if pixels[x, y] == c_sand:
                pixels[x, y] = c_transparent
    img.save(img_path)

base_dir = "/Users/henrique/pokedungeon/assets/tiles/"
# Recreate corners with transparent background
recreate_corner(base_dir + "water_nw.png", "nw")
recreate_corner(base_dir + "water_ne.png", "ne")
recreate_corner(base_dir + "water_sw.png", "sw")
recreate_corner(base_dir + "water_se.png", "se")

# Remove sand from the straight edges
remove_sand(base_dir + "water_n.png")
remove_sand(base_dir + "water_s.png")
remove_sand(base_dir + "water_e.png")
remove_sand(base_dir + "water_w.png")

print("Sand borders removed and made transparent!")
