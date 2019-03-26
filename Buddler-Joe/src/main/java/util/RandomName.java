package util;

import java.util.Random;

/**
 * Generate a random name composed of an adjective and an animal. Temporarily used to generate
 * usernames.
 */
public class RandomName {
  private static final String[] adjectives =
      new String[] {
        "able", "aching", "acidic", "active", "actual", "adept", "admired", "adored", "afraid",
        "aged", "agile", "ajar", "alarmed", "alert", "alive", "all", "amazing", "ample", "amused",
        "amusing", "ancient", "angelic", "angry", "annual", "another", "antique", "anxious", "any",
        "apt", "arctic", "arid", "ashamed", "assured", "austere", "average", "aware", "awesome",
        "awful", "awkward", "babyish", "back", "bad", "baggy", "bare", "barren", "basic", "belated",
        "beloved", "best", "better", "big", "bitter", "black", "bland", "blank", "blaring", "bleak",
        "blind", "blond", "blue", "bogus", "boiling", "bold", "bony", "boring", "bossy", "both",
        "bouncy", "bowed", "brave", "brief", "bright", "brisk", "broken", "bronze", "brown",
        "bruised", "bubbly", "bulky", "bumpy", "buoyant", "burly", "busy", "buttery", "buzzing",
        "calm", "candid", "canine", "capital", "careful", "caring", "cheap", "cheery", "chief",
        "chilly", "chubby", "classic", "clean", "clear", "clever", "close", "closed", "cloudy",
        "clumsy", "coarse", "cold", "common", "complex", "content", "cooked", "cool", "corny",
        "corrupt", "costly", "crafty", "crazy", "creamy", "creepy", "crisp", "crooked", "crowded",
        "cruel", "cuddly", "curly", "curvy", "cute", "damaged", "damp", "dapper", "daring", "dark",
        "darling", "dead", "deadly", "dear", "dearest", "decent", "decimal", "deep", "defiant",
        "delayed", "dense", "dental", "devoted", "digital", "dim", "dimpled", "direct", "dirty",
        "dismal", "distant", "dizzy", "dopey", "doting", "double", "drab", "drafty", "dreary",
        "droopy", "dry", "dual", "dull", "dutiful", "each", "eager", "early", "earnest", "easy",
        "edible", "elastic", "elated", "elderly", "elegant", "eminent", "empty", "enraged",
        "entire", "envious", "equal", "ethical", "even", "every", "evil", "exalted", "excited",
        "exotic", "expert", "failing", "faint", "fair", "fake", "false", "famous", "fancy", "far",
        "far-off", "faraway", "fast", "fat", "fatal", "fearful", "feisty", "feline", "female",
        "few", "fickle", "filthy", "fine", "firm", "first", "fitting", "fixed", "flaky", "flashy",
        "flat", "flawed", "flimsy", "flowery", "fluffy", "fluid", "focused", "fond", "foolish",
        "forked", "formal", "frail", "frank", "frayed", "free", "French", "fresh", "frigid",
        "frilly", "frizzy", "front", "frosty", "frozen", "frugal", "full", "funny", "fussy",
        "fuzzy", "gaseous", "general", "gentle", "genuine", "giant", "giddy", "gifted", "giving",
        "glaring", "glass", "gleeful", "gloomy", "glossy", "glum", "golden", "good", "grand",
        "grave", "gray", "great", "greedy", "green", "grim", "grimy", "gross", "grouchy", "growing",
        "grown", "grubby", "grumpy", "guilty", "gummy", "hairy", "half", "handy", "happy", "hard",
        "harmful", "harsh", "hasty", "hateful", "healthy", "hearty", "heavy", "hefty", "helpful",
        "hidden", "hideous", "high", "hoarse", "hollow", "homely", "honest", "honored", "hopeful",
        "hot", "huge", "humble", "humming", "hungry", "hurtful", "husky", "icky", "icy", "ideal",
        "idiotic", "idle", "ill", "illegal", "immense", "impish", "impure", "inborn", "intent",
        "itchy", "jaded", "jagged", "jaunty", "jealous", "jittery", "joint", "jolly", "jovial",
        "joyful", "joyous", "juicy", "jumbo", "jumpy", "junior", "keen", "key", "kind", "kindly",
        "klutzy", "knobby", "knotty", "knowing", "known", "kooky", "kosher", "lame", "lanky",
        "large", "last", "lasting", "late", "lavish", "lawful", "lazy", "leading", "leafy", "lean",
        "left", "legal", "light", "likable", "likely", "limited", "limp", "limping", "linear",
        "lined", "liquid", "little", "live", "lively", "livid", "lone", "lonely", "long", "loose",
        "lost", "loud", "lovable", "lovely", "loving", "low", "loyal", "lucky", "lumpy", "mad",
        "made-up", "major", "male", "mammoth", "married", "massive", "mature", "meager", "mealy",
        "mean", "measly", "meaty", "medical", "medium", "meek", "mellow", "melodic", "merry",
        "messy", "mild", "milky", "minor", "minty", "miserly", "misty", "mixed", "modern", "modest",
        "moist", "monthly", "moral", "muddy", "muffled", "mundane", "murky", "mushy", "musty",
        "muted", "naive", "narrow", "nasty", "natural", "naughty", "near", "neat", "needy",
        "nervous", "new", "next", "nice", "nifty", "nimble", "nippy", "noisy", "nonstop", "normal",
        "notable", "noted", "novel", "noxious", "numb", "nutty", "obese", "oblong", "obvious",
        "odd", "oddball", "offbeat", "oily", "old", "only", "open", "optimal", "opulent", "orange",
        "orderly", "organic", "ornate", "ornery", "other", "our", "oval", "overdue", "pale",
        "paltry", "parched", "partial", "past", "pastel", "peppery", "perfect", "perky", "pesky",
        "petty", "phony", "pink", "pitiful", "plain", "plastic", "playful", "pleased", "plump",
        "plush", "pointed", "poised", "polite", "poor", "popular", "portly", "posh", "potable",
        "present", "pretty", "pricey", "prickly", "primary", "prime", "private", "prize", "profuse",
        "proper", "proud", "prudent", "pungent", "puny", "pure", "purple", "pushy", "putrid",
        "puzzled", "quaint", "queasy", "quick", "quiet", "quirky", "radiant", "ragged", "rapid",
        "rare", "rash", "raw", "ready", "real", "recent", "red", "regal", "regular", "remote",
        "rich", "right", "rigid", "ringed", "ripe", "roasted", "robust", "rosy", "rotten", "rough",
        "round", "rowdy", "royal", "rubbery", "ruddy", "rude", "rundown", "runny", "rural", "rusty",
        "sad", "safe", "salty", "same", "sandy", "sane", "scaly", "scarce", "scared", "scary",
        "scented", "scrawny", "second", "secret", "selfish", "serene", "serious", "several",
        "severe", "shabby", "shadowy", "shady", "shallow", "sharp", "shiny", "shocked", "shoddy",
        "short", "showy", "shrill", "shy", "sick", "silent", "silky", "silly", "silver", "similar",
        "simple", "sinful", "single", "skinny", "sleepy", "slight", "slim", "slimy", "slow",
        "slushy", "small", "smart", "smoggy", "smooth", "smug", "snappy", "sneaky", "snoopy",
        "soft", "soggy", "solid", "somber", "some", "sore", "soulful", "soupy", "sour", "Spanish",
        "sparse", "speedy", "spicy", "spiffy", "spotted", "spry", "square", "squeaky", "stable",
        "staid", "stained", "stale", "starchy", "stark", "starry", "steel", "steep", "sticky",
        "stiff", "stingy", "stormy", "strange", "strict", "striped", "strong", "stupid", "sturdy",
        "stylish", "subdued", "subtle", "sudden", "sugary", "sunny", "super", "superb", "svelte",
        "sweaty", "sweet", "swift", "tall", "tame", "tan", "tart", "tasty", "taut", "tedious",
        "teeming", "tender", "tense", "tepid", "testy", "that", "these", "thick", "thin", "third",
        "thirsty", "this", "thorny", "those", "thrifty", "tidy", "tight", "timely", "tinted",
        "tiny", "tired", "torn", "total", "tough", "tragic", "trained", "tricky", "trim", "trivial",
        "true", "trusty", "tubby", "twin", "ugly", "unaware", "uneven", "unfit", "unhappy",
        "uniform", "unique", "united", "unkempt", "unknown", "unlined", "unlucky", "unripe",
        "unruly", "unsung", "untidy", "untried", "untrue", "unused", "unusual", "upbeat", "upright",
        "upset", "urban", "usable", "used", "useful", "useless", "utter", "vacant", "vague", "vain",
        "valid", "vapid", "vast", "velvety", "vibrant", "vicious", "violent", "violet", "virtual",
        "visible", "vital", "vivid", "wan", "warlike", "warm", "warped", "wary", "watery", "wavy",
        "weak", "wealthy", "weary", "webbed", "wee", "weekly", "weepy", "weighty", "weird",
        "welcome", "wet", "which", "white", "whole", "wicked", "wide", "wiggly", "wild", "willing",
        "wilted", "winding", "windy", "winged", "wiry", "wise", "witty", "wobbly", "woeful",
        "wooden", "woozy", "wordy", "worldly", "worn", "worried", "worse", "worst", "worthy",
        "wrong", "wry", "yawning", "yearly", "yellow", "young", "yummy", "zany", "zealous", "zesty",
        "zigzag"
      };
  private static final String[] animals =
      new String[] {
        "frog", "newt", "tadpole", "toad", "spider", "biddy", "canary", "crow", "cuckoo", "dove,",
        "pigeon", "duck", "eagle", "falcon", "finch", "goose", "gull", "hawk", "jackdaw", "jay",
        "kestrel", "mallard", "ostrich", "owl", "parrot", "peacock", "pelican", "penguin",
        "piranha", "raven", "robin", "rooster", "sparrow", "stork", "swallow", "swan", "swift",
        "tit", "turkey", "vulture", "wren", "barbel", "carp", "cod", "crab", "eel", "haddock",
        "halibut", "lobster", "perch", "pike", "plaice", "ray", "salmon", "sawfish", "scallop",
        "shark", "shell", "shrimp", "trout", "ant", "aphid", "bee", "beetle", "flea", "fly",
        "gadfly", "ladybug", "larva", "maggot", "midge", "moth", "nymph", "wasp", "badger", "bat",
        "bear", "beaver", "bullock", "camel", "dolphin", "fox", "gazelle", "gerbil", "giraffe",
        "goat", "hamster", "hare", "hare", "horse", "hyena", "lion", "llama", "lynx", "mammoth",
        "marmot", "mink", "mole", "mouse", "mule", "otter", "panda", "polecat", "pony", "puma",
        "racoon", "rat", "seal", "seal", "sheep", "skunk", "sloth", "tiger", "weasel", "whale",
        "wolf", "zebra", "slug", "snail", "boa", "gecko", "iguana", "lizard", "python", "saurian",
        "snake", "leech"
      };

  /**
   * Returns a random name with an adjective and animal name in it. Still need to check if it is
   * unique!
   *
   * @return random name
   */
  public static String getRandomName() {
    Random random = new Random();

    String adj1 = adjectives[random.nextInt(adjectives.length)];
    adj1 = adj1.substring(0, 1).toUpperCase() + adj1.substring(1);

    // String adj2 = adjectives[random.nextInt(adjectives.length)];
    // adj2 = adj2.substring(0, 1).toUpperCase() + adj2.substring(1);

    String animal = animals[random.nextInt(animals.length)];
    animal = animal.substring(0, 1).toUpperCase() + animal.substring(1);

    return adj1 + animal;
  }
}
