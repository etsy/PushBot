require(['js/vendor/less-1.1.0.min.js',
         'js/vendor/jquery-1.6.1.min.js'], function() {
  require(['js/vendor/underscore-min.js',
           'js/PushBotConfig.js',
           'js/vendor/jquery.log.js',
           'js/vendor/underscore.string.min.js',
           'js/vendor/jquery.address-1.4.min.js',
           'js/vendor/jquery.timeago.js',
           'js/vendor/jquery.flot-0.1.pack.js',
           'js/vendor/jquery.tmpl.min.js'], function() {
               require.ready(function() {
                    PushBotConfig.init();
               });
           });
         });
