import ExpressConfig from "./configs/express.config.js"



const app = ExpressConfig();
const PORT = process.env.PORT || 8080;

function printInformations() {
    console.log(`
    __  __ __   ____  ____  ____   __ __   ___   ______    ___       ____  ____  ____ 
   /  ]|  T  T /    Tl    j|    \\ |  T  | /   \\ |      T  /  _]     /    T|    \\l    j
  /  / |  l  |Y  o  | |  T |  _  Y|  |  |Y     Y|      | /  [_     Y  o  ||  o  )|  T 
 /  /  |  _  ||     | |  | |  |  ||  |  ||  O  |l_j  l_jY    _]    |     ||   _/ |  | 
/   \\_ |  |  ||  _  | |  | |  |  |l  :  !|     |  |  |  |   [_     |  _  ||  |   |  | 
\\     ||  |  ||  |  | j  l |  |  | \\   / l     !  |  |  |     T    |  |  ||  |   j  l 
 \\____jl__j__jl__j__j|____jl__j__j  \\_/   \\___/   l__j  l_____j    l__j__jl__j  |____j
                                                                                                   
    `);
    console.log("DS Project - 2022/2023");
    console.log("Authors: Giovanni Antonioni, Luca Tassinari, Luca Rubboli");
    console.log("Server Running on Port " + PORT);
}
app.listen(PORT, printInformations);
