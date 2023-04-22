const path = require('path');
const webpack = require('webpack');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");

module.exports = () => {
    return {
        entry: './src/main/js/app.js',
        devtool: 'eval-source-map',
        cache: true,
        mode: 'development',
        output: {
            filename: 'bundle.js'
        },
        resolve: {
            extensions: ['*', '.js']
        },
        module: {
            rules: [
                //Sass
                {
                    test: /\.(scss|css)$/,
                    use: ['style-loader', 'css-loader', 'postcss-loader', 'sass-loader'],
                },
                //Babel
                {
                    test: /\.(js|jsx|ts|tsx)$/,
                    exclude: /(node_modules|bower_components)/,
                    use: [{
                        loader: 'babel-loader',
                        options: {
                            presets: [
                                "@babel/preset-env",
                                ["@babel/preset-react", {"runtime": "automatic"}]
                            ]
                        }
                    }]
                },
                // Images
                {
                    test: /\.(?:ico|gif|png|jpg|jpeg|webp)$/,
                    use: [
                        {
                            loader: 'file-loader',
                            options: {
                                name: '[name].[ext]',
                                outputPath: 'images/'
                            }
                        }
                    ]
                },
                // Fonts
                {
                    test: /\.(woff(2)?|eot|ttf|otf|svg|)$/,
                    use: [
                        {
                            loader: 'file-loader',
                            options: {
                                name: '[name].[ext]',
                                outputPath: 'fonts/'
                            }
                        }
                    ]
                },
            ]
        },
        plugins: [
            new MiniCssExtractPlugin({
                filename: 'style.css',
                chunkFilename: '[id].css'
            })
        ]
    }
};