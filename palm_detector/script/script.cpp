#include <iostream>
#include <cstdlib>
#include <ctime>

int main(int argc, char *argv[]) {
    // Seed the random number generator
    std::srand(std::time(0));

    // Check if two image paths are provided as command line arguments
    if (argc != 3) {
        std::cerr << "Usage: " << argv[0] << " <image_path1> <image_path2>" << std::endl;
        return 1;
    }

    // Get the image paths from command line arguments
    std::string imagePath1 = argv[1];
    std::string imagePath2 = argv[2];

    // Generate a random boolean value (true or false)
    bool result = std::rand() % 2 == 0;

    // Output the result
    std::cout << (result ? "true" : "false") << std::endl;

    // Return 0 to indicate successful execution
    return 0;
}
