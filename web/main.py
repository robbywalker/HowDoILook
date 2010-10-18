# Copyright 2010 The HowDoILook Authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

from google.appengine.ext import db
from google.appengine.ext import webapp
from google.appengine.ext.webapp import template
from google.appengine.ext.webapp import util

import hashlib
import os
import png
import sys



class Screen(db.Model):
    name = db.StringProperty()
    hash = db.StringProperty()
    image = db.BlobProperty()
    ignoredRegions = db.StringListProperty()



class BaseHandler(webapp.RequestHandler):
  def renderTemplate(self, name, **template_values):
    path = os.path.join(os.path.dirname(__file__), name)
    self.response.out.write(template.render(path, template_values))



class MainHandler(BaseHandler):
  def get(self):
    screens = Screen.all()
    self.renderTemplate('templates/index.html', screens=screens)




class ScreenHandler(BaseHandler):
  def get(self, key):
    screen = Screen.get(key)
    self.renderTemplate('templates/screen.html', screen=screen)



class ImageHandler(BaseHandler):
  def get(self, key):
    screen = Screen.get(key)
    self.response.headers['Content-Type'] = 'image/png'
    self.response.out.write(screen.image)



class UploadHandler(BaseHandler):
  def get(self):
    self.renderTemplate('templates/upload.html')

  def post(self):
    name = self.request.get('name')
    img = self.request.get('img')

    if not name or not img:
      self.response.out.write('Name and image are both required.')
    else:
      pixels = png.Reader(bytes=img).asRGB()[2]
      m = hashlib.md5()
      for pixel in pixels:
        # TODO(robbyw): Ignore regions.
        m.update(pixel.tostring())
      new_hash = m.hexdigest()

      query = Screen.all().filter('name = ', name)
      screen = None
      different = False
      if query.count():
        screen = query.fetch(1)[0]
        if screen.hash != new_hash:
          # TODO(robbyw): Store something here.
          different = True
      else:
        screen = Screen()

      screen.name = name
      screen.hash = new_hash
      screen.image = db.Blob(img)
      screen.ignoredRegions = []

      screen.put()

      if different:
        self.response.out.write("Changed: %s" % new_hash)
      else:
        self.response.out.write("Same: %s" % new_hash)




def main():
  application = webapp.WSGIApplication(
      [('/', MainHandler),
       ('/screen/(.*)', ScreenHandler),
       ('/image/(.*)', ImageHandler),
       ('/upload', UploadHandler)
       ],
      debug=True)
  util.run_wsgi_app(application)

if __name__ == "__main__":
  main()
