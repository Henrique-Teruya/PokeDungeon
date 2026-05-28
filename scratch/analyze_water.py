from PIL import Image
import collections

img = Image.open("/Users/henrique/pokedungeon/assets/tiles/water_nw.png").convert("RGBA")
pixels = img.load()
w, h = img.size

colors = collections.Counter()
for y in range(h):
    for x in range(w):
        colors[pixels[x, y]] += 1

print("Colors in water_nw.png:", colors.most_common())
print("Top row colors:")
for x in range(w):
    print(pixels[x, 0], end=" ")
print("\nLeft column colors:")
for y in range(h):
    print(pixels[0, y], end=" ")
