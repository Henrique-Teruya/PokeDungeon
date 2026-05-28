import math
from PIL import Image

def recreate_corner(img_path, corner):
    img = Image.new("RGBA", (32, 32))
    pixels = img.load()
    w, h = 32, 32
    
    c_sand = (200, 184, 136, 255)
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
            
            # The straight border is at index 4 (0, 1, 2, 3 are sand).
            # If we want the border to land at 4, the distance from 31.5 to 4 is 27.5.
            if dist >= 27.8:
                pixels[x, y] = c_sand
            elif dist >= 26.5:
                pixels[x, y] = c_border
            else:
                pixels[x, y] = c_water
                
    img.save(img_path)

base_dir = "/Users/henrique/pokedungeon/assets/tiles/"
recreate_corner(base_dir + "water_nw.png", "nw")
recreate_corner(base_dir + "water_ne.png", "ne")
recreate_corner(base_dir + "water_sw.png", "sw")
recreate_corner(base_dir + "water_se.png", "se")
print("Corners recreated with drawn borders!")
