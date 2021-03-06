let path = require('path');
let webpack = require('webpack');
let { VueLoaderPlugin } = require("vue-loader");

require("babel-polyfill");

module.exports = {
  entry: ['babel-polyfill', './src/main.js'],
  output: {
    path: path.resolve(__dirname, './dist'),
    publicPath: '/dist/',
    filename: 'build.js'
  },
  module: {
    rules: [
      {
        test: /\.css$/,
        use: [
          'vue-style-loader',
          'css-loader',
          'postcss-loader'
        ],
      },
      {
        test: /\.scss$/,
        use: [
          'vue-style-loader',
          'css-loader',
          'postcss-loader',
          'sass-loader'
        ],
      },
      {
        test: /\.sass$/,
        use: [
          'vue-style-loader',
          'css-loader',
          'postcss-loader',
          'sass-loader?indentedSyntax'
        ],
      },
      {
        test: /\.vue$/,
        loader: 'vue-loader',
        options: {
          loaders: {
            // Since sass-loader (weirdly) has SCSS as its default parse mode, we map
            // the "scss" and "sass" values for the lang attribute to the right configs here.
            // other preprocessors should work out of the box, no loader config like this necessary.
            'scss': [
              'vue-style-loader',
              'css-loader',
              'postcss-loader',
              'sass-loader'
            ],
            'sass': [
              'vue-style-loader',
              'css-loader',
              'postcss-loader',
              'sass-loader?indentedSyntax'
            ]
          }
          // other vue-loader options go here
        },
      },
      {
        test: /\.js$/,
        loader: 'babel-loader',
        exclude: /node_modules/
      },
      {
        test: /\.jpe?g$|\.ico$|\.gif$|\.png$|\.svg$|\.woff$|\.ttf$|\.wav$|\.mp3$/,
        loader: 'file-loader',
        options: {
          name: '[name].[ext]?[hash]'
        }
      }
    ]
  },
  resolve: {
    alias: {
      'vue$': 'vue/dist/vue.esm.js',
      '@': path.resolve(__dirname, 'assets'),
      '#': path.resolve(__dirname, 'src')
    },
    extensions: ['*', '.js', '.vue', '.json']
  },
  devServer: {
    historyApiFallback: true,
    noInfo: true,
    overlay: true,
    host: '0.0.0.0', // Host webpack dev server'a
    port: 3000, // Port webpack dev server'a
    hot: true,
    proxy: {
      'http://localhost/api/*': {
        target: `http://localhost:8080`, // ?????????????? ?????????? ???????????????????? ??????????????, ???????? ?????????? ???????????????????????????? ?????????????? ?? ?????? ??????????????
        onProxyRes(proxyRes, req, res) {
          if (proxyRes.headers.location) { // ???????? ???????? ?????????????????? ???? ?????????????????? location (?????? ?????????????????? ???????????? ?????????? ?????????????? ?? ???????????????????? ??????????????)
            let location = proxyRes.headers.location; // ?????????????????? ?????????? ?????????????????? location ???? ???????????? ?? ???????????????????? location
            console.log(`LOCATION: ${proxyRes.headers.location}`); // ?????????????? ?? ?????????????? ?????????? ???? ????????????
            location = location.replace('localhost:8080', 'localhost:3000'); // ???????????????? ?????????? ???????????????????? ?????????????? ???? ?????????? webpack dev server'a
            proxyRes.headers.location = location; // ?????????????????????? ?? ?????????????????? location ?????????????????????? ?????????? ???? ???????????????????? location
            console.log(`REPLACED: ${proxyRes.headers.location}`); // ?????????????? ?? ?????????????? ?????????? ?????????? ????????????
          }
        }
      }
    }
  },
  plugins: [
    new webpack.HotModuleReplacementPlugin(),
    new VueLoaderPlugin()
  ],
  performance: {
    hints: false
  },
  optimization: {
    minimize: true
  },
  devtool: '#eval-source-map',
};

if (process.env.NODE_ENV === 'production') {
  module.exports.devtool = '#source-map';
  // http://vue-loader.vuejs.org/en/workflow/production.html
  module.exports.plugins = (module.exports.plugins || []).concat([
    new webpack.DefinePlugin({
      'process.env': {
        NODE_ENV: '"production"'
      }
    }),
    new webpack.LoaderOptionsPlugin({
      minimize: true
    })
  ])
}
