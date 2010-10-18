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

    lastGoodHash = db.StringProperty()
    lastGoodImage = db.BlobProperty()

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



class ApprovedImageHandler(BaseHandler):
  def get(self, key):
    screen = Screen.get(key)
    self.response.headers['Content-Type'] = 'image/png'
    self.response.out.write(screen.lastGoodImage)



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
      state = 'Same'
      if query.count():
        screen = query.fetch(1)[0]
        if screen.hash != new_hash:
          if screen.lastGoodHash == new_hash:
            # A change was reverted.
            state = 'same'
            screen.image = screen.lastGoodImage
            screen.hash = screen.lastGoodHash
            screen.lastGoodImage = None
            screen.lastGoodHash = None

          else:
            state = 'Changed'
            if not screen.lastGoodImage:
              # This is a new change.
              screen.lastGoodImage = screen.image
              screen.lastGoodHash = screen.hash
            screen.hash = new_hash
            screen.image = db.Blob(img)

      else:
        state = 'New'
        screen = Screen()
        screen.name = name
        screen.ignoredRegions = []
        screen.hash = new_hash
        screen.image = db.Blob(img)

      screen.put()

      self.response.out.write("%s: %s" % (state, new_hash))




def main():
  application = webapp.WSGIApplication(
      [('/', MainHandler),
       ('/screen/(.*)', ScreenHandler),
       ('/image/(.*)', ImageHandler),
       ('/approvedImage/(.*)', ApprovedImageHandler),
       ('/upload', UploadHandler)
       ],
      debug=True)
  util.run_wsgi_app(application)

if __name__ == "__main__":
  main()
