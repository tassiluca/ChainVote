import ExpressConfig from "./configs/express.config";

const app = ExpressConfig();
const PORT = process.env.PORT || 8180;

function printInformations() {
    console.log(`    
    __  __ __   ____  ____  ____   __ __   ___   ______    ___       ____  __ __  ______  __ __ 
   /  ]|  T  T /    Tl    j|    \\ |  T  | /   \\ |      T  /  _]     /    T|  T  T|      T|  T  T
  /  / |  l  |Y  o  | |  T |  _  Y|  |  |Y     Y|      | /  [_     Y  o  ||  |  ||      ||  l  |
 /  /  |  _  ||     | |  | |  |  ||  |  ||  O  |l_j  l_jY    _]    |     ||  |  |l_j  l_j|  _  |
/   \\_ |  |  ||  _  | |  | |  |  |l  :  !|     |  |  |  |   [_     |  _  ||  :  |  |  |  |  |  |
\\     ||  |  ||  |  | j  l |  |  | \\   / l     !  |  |  |     T    |  |  |l     |  |  |  |  |  |
 \\____jl__j__jl__j__j|____jl__j__j  \\_/   \\___/   l__j  l_____j    l__j__j \\__,_j  l__j  l__j__j
 `);
    console.log("DS Project - 2022/2023");
    console.log("Authors: Giovanni Antonioni, Luca Tassinari, Luca Rubboli");
    console.log("Server Running on Port " + PORT);
}
app.listen(PORT, printInformations);
