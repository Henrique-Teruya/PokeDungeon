from PIL import Image

img_n = Image.open("/Users/henrique/pokedungeon/assets/tiles/water_n.png").convert("RGBA")
pixels_n = img_n.load()
print("water_n.png center column:")
for y in range(img_n.size[1]):
    print(pixels_n[16, y], end=" ")
print("\n")

img_w = Image.open("/Users/henrique/pokedungeon/assets/tiles/water_w.png").convert("RGBA")
pixels_w = img_w.load()
print("water_w.png center row:")
for x in range(img_w.size[0]):
    print(pixels_w[x, 16], end=" ")
print("\n")
