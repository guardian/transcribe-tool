const path = require('path');

module.exports = {
    mode: "development",
    entry: "./src/App.js",
    output: {
        path: path.resolve(__dirname, "../public/dist"),
        filename: "bundle.js", // string
    },
    module: {
        rules: [
        {
            test: /.jsx?$/,
            exclude: /(node_modules)/,
            use: {
            loader: 'babel-loader',
            options: {
                presets: ['@babel/preset-env']
            }
            }
        },
        {
            test: /\.css$/,
            use: ['style-loader', 
                {
                    "loader": 'css-loader',
                    options: {
                        modules: true,
                    }
                }
        ],
            
        }
        ]
  }
}