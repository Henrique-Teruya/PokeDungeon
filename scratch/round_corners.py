from PIL import Image
import math

def round_corner(img_path, corner, radius=10):
    img = Image.open(img_path).convert("RGBA")
    pixels = img.load()
    w, h = img.size
    
    for y in range(h):
        for x in range(w):
            if corner == "nw":
                if x < radius and y < radius:
                    dist = math.hypot(radius - 0.5 - x, radius - 0.5 - y)
                    if dist > radius:
                        pixels[x, y] = (0, 0, 0, 0)
            elif corner == "ne":
                if x >= w - radius and y < radius:
                    dist = math.hypot(x - (w - radius - 0.5), radius - 0.5 - y)
                    if dist > radius:
                        pixels[x, y] = (0, 0, 0, 0)
            elif corner == "sw":
                if x < radius and y >= h - radius:
                    dist = math.hypot(radius - 0.5 - x, y - (h - radius - 0.5))
                    if dist > radius:
                        pixels[x, y] = (0, 0, 0, 0)
            elif corner == "se":
                if x >= w - radius and y >= h - radius:
                    dist = math.hypot(x - (w - radius - 0.5), y - (h - radius - 0.5))
                    if dist > radius:
                        pixels[x, y] = (0, 0, 0, 0)

    img.save(img_path)

base_dir = "/Users/henrique/pokedungeon/assets/tiles/"
round_corner(base_dir + "water_nw.png", "nw", 10)
round_corner(base_dir + "water_ne.png", "ne", 10)
round_corner(base_dir + "water_sw.png", "sw", 10)
round_corner(base_dir + "water_se.png", "se", 10)
print("Rounded corners generated successfully!")
